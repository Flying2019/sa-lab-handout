"""Trusted host-side Docker grading. Never execute a submission's judge or timeout."""
import argparse
import json
from pathlib import Path
import subprocess
import time
import uuid
from script import runner

ROOT = Path(__file__).resolve().parent


def control(docker, arguments, timeout=15):
    return subprocess.run([docker, *arguments], capture_output=True, text=True,
                          timeout=timeout, check=False)


def container_job(docker, image, command, timeout, log, mounts=(), env=(),
                  output=None, snapshot=None):
    name = 'sa-case-'+uuid.uuid4().hex
    started = time.monotonic()
    result = {'status':'launch_error'}
    try:
        arguments = ['create', '--name', name, '--network', 'bridge' if snapshot else 'none',
            '--memory', '4g', '--pids-limit', '256', '--cpus', '2',
            '--workdir', '/workspace', '--entrypoint', '/bin/sh']
        for mount in mounts: arguments.extend(['--mount',mount])
        for value in env: arguments.extend(['--env',value])
        created = control(docker, [*arguments,image,*command], timeout=min(30,timeout))
        if created.returncode:
            Path(log).write_text(created.stderr,encoding='utf-8')
            return result
        remaining = timeout-(time.monotonic()-started)
        if remaining <= 0: return {'status':'timeout'}
        result = runner.run([docker,'start','--attach',name], ROOT, log, remaining,
                            output)
        # docker start's own status is not the application's exit code.
        if result['status']=='ok':
            remaining = timeout-(time.monotonic()-started)
            if remaining <= 0: return {'status':'timeout'}
            state = control(docker,['inspect','--format','{{json .State}}',name],
                            timeout=min(15,remaining))
            info = json.loads(state.stdout) if state.returncode==0 else {}
            if info.get('Running') or info.get('ExitCode') != 0:
                result = {'status':'crash'}
        if result['status']=='ok' and snapshot:
            remaining = timeout-(time.monotonic()-started)
            if remaining <= 0: return {'status':'timeout'}
            committed=control(docker,['commit',name,snapshot],timeout=remaining)
            if committed.returncode: result={'status':'snapshot_error'}
        return result
    except subprocess.TimeoutExpired:
        return {'status':'timeout'}
    except (OSError, ValueError):
        return {'status':'launch_error'}
    finally:
        # Kill the container, not merely the attached Docker CLI process.
        # A submission that spawns detached children remains bounded by this removal.
        removed = control(docker,['rm','--force',name],timeout=30)
        if removed.returncode and 'No such container' not in removed.stderr:
            raise RuntimeError('Could not remove grading container '+name+': '+removed.stderr)


def build_project(docker,image,snapshot,timeout,log):
    return container_job(docker,image,['./run_build.sh'],timeout,log,snapshot=snapshot)


def run_container(docker,image,source,output_dir,case,timeout,log):
    target='/input/'+case['profile']+'/'+source.name
    return container_job(docker,image,['./run_test.sh',target,'/output/result.txt'],timeout,log,
        mounts=[f'type=bind,source={source.resolve()},target={target},readonly',
                f'type=bind,source={output_dir.resolve()},target=/output'],
        env=['SA_PROFILE='+case['profile'],'SA_CONTEXT='+case.get('context','')],
        output=output_dir/'result.txt')


def grade(args):
    submission=args.submission.resolve()
    if not all((submission/name).is_file() for name in ('Dockerfile','run_build.sh','run_test.sh')):
        raise ValueError('Dockerfile, run_build.sh and run_test.sh are required')
    items=runner.cases(args.cases,args.profile)
    home=args.output.resolve()
    home.mkdir(parents=True,exist_ok=False)
    image='sa-submission:'+uuid.uuid4().hex
    snapshot='sa-built:'+uuid.uuid4().hex
    records=[]
    try:
        built=runner.run([args.docker,'build','--tag',image,str(submission)],ROOT,
                         home/'image.log',args.build_timeout)
        if built['status']=='ok':
            built=build_project(args.docker,image,snapshot,args.build_timeout,home/'build.log')
        for item in items:
            _,case,_,_,_=item
            dest=home/case['id'];dest.mkdir()
            inputs=dest/'input';output=dest/'output';output.mkdir()
            output.chmod(0o777)
            execution={'status':built['status']}
            score=0.0
            if built['status']=='ok':
                try:
                    runner.prepare(item,inputs,args.javac,args.java)
                    execution=run_container(args.docker,snapshot,item[2][0],output,case,
                                            args.timeout,dest/'container.log')
                    # Replay uses trusted, host-compiled test classes, never files from /output.
                    score=runner.assess_output(execution,output/'result.txt',item,inputs,dest,args.java)
                except (OSError,ValueError):
                    execution={'status':'error'}
            records.append({'case':case['id'],'score':score,'max_score':2,
                            'status':execution['status']})
            print(f'{case["id"]}: {score:.6f}/2',flush=True)
            report={'score':sum(r['score'] for r in records),'max_score':2*len(items),'results':records}
            (home/'report.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
        print(f'SCORE {report["score"]:.6f}/{report["max_score"]}',flush=True)
        return 0 if report['score']==report['max_score'] else 1
    finally:
        control(args.docker,['image','rm','--force',snapshot,image],timeout=30)


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--submission',type=Path,required=True)
    parser.add_argument('--cases',type=Path,default=ROOT/'hidden')
    parser.add_argument('--profile',choices=['all',*runner.PROFILES],default='all')
    parser.add_argument('--output',type=Path,required=True,help='new report directory')
    parser.add_argument('--docker',default='docker')
    parser.add_argument('--java',default='java')
    parser.add_argument('--javac',default='javac')
    parser.add_argument('--build-timeout',type=float,default=900)
    parser.add_argument('--timeout',type=float,default=120,
                        help='external per-case deadline, excluding the one-time project build')
    args=parser.parse_args()
    if min(args.timeout,args.build_timeout)<=0: parser.error('timeouts must be positive')
    try: return grade(args)
    except (OSError,ValueError,RuntimeError,subprocess.TimeoutExpired):
        print('ERROR')
        return 1


if __name__=='__main__':
    raise SystemExit(main())
