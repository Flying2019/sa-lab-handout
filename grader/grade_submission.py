"""Disposable Docker grading; host deadlines, copied inputs, no bind mounts."""
import argparse
import json
from pathlib import Path
import shutil
import subprocess
import tarfile
import tempfile
import time
import uuid
from script import runner

ROOT = Path(__file__).resolve().parent
CACHES = {'.git', '.work', '.gradle', 'build', '__pycache__', '.venv',
          '.idea', '.vscode', '.env', 'compose.override.yaml'}
LIMIT = 2_000_000


def below(path, parent):
    return path == parent or parent in path.parents


def control(docker, arguments, timeout=15):
    return subprocess.run([docker, *arguments], capture_output=True, text=True,
                          timeout=timeout, check=False)


def copy_context(submission, dockerfile, target):
    def ignored(folder, names):
        skip = CACHES | {name for name in names if name.endswith('.pyc')}
        if Path(folder) == submission: skip |= {'tests'}
        if Path(folder) == ROOT: skip |= {'hidden'}
        return skip.intersection(names)
    shutil.copytree(submission, target/'handout', ignore=ignored)
    shutil.copytree(ROOT, target/'grader', ignore=ignored)
    for path in (target/'handout').rglob('*'):
        if path.is_file() and (path.suffix=='.sh' or path.name=='gradlew'):
            path.write_bytes(path.read_bytes().replace(b'\r\n',b'\n'))
    shutil.copy2(dockerfile, target/'Dockerfile')
    ignore = dockerfile.parent/'.dockerignore'
    if ignore.is_file(): shutil.copy2(ignore, target/'.dockerignore')


def copy_output(docker, name, remote, local, timeout):
    # Read a bounded tar stream without extracting paths or preserving ownership.
    archive = local.with_suffix('.tar')
    try:
        result = runner.run([docker, 'cp', name+':'+remote, '-'], ROOT, archive,
                            timeout, limit=LIMIT+20_480)
        if result['status'] in {'timeout', 'output_limit'}: return result
        if result['status'] != 'ok': return {'status':'ok'}  # missing output
        with tarfile.open(archive) as contents:
            members = contents.getmembers()
            if len(members) != 1 or not members[0].isfile(): return {'status':'ok'}
            member = members[0]
            if member.size > LIMIT: return {'status':'output_limit'}
            local.write_bytes(contents.extractfile(member).read())
        return {'status':'ok'}
    except tarfile.TarError:
        return {'status':'ok'}
    finally:
        archive.unlink(missing_ok=True)


def container_job(docker, image, command, timeout, log, uploads=(), env=(),
                  output=None, snapshot=None, network='none'):
    name = 'sa-case-'+uuid.uuid4().hex
    started = time.monotonic()
    try:
        arguments = ['create', '--name', name, '--network', network,
            '--memory', '4g', '--pids-limit', '256', '--cpus', '2',
            '--workdir', '/workspace/handout', '--entrypoint', '/bin/sh']
        for value in env: arguments.extend(['--env',value])
        created = control(docker, [*arguments,image,*command], timeout=min(30,timeout))
        if created.returncode:
            Path(log).write_text(created.stderr,encoding='utf-8')
            return {'status':'launch_error'}
        for source, target in uploads:
            remaining = timeout-(time.monotonic()-started)
            if remaining <= 0: return {'status':'timeout'}
            copied = control(docker, ['cp',str(source),name+':'+target],timeout=remaining)
            if copied.returncode:
                Path(log).write_text(copied.stderr,encoding='utf-8')
                return {'status':'copy_error'}
        remaining = timeout-(time.monotonic()-started)
        if remaining <= 0: return {'status':'timeout'}
        result = runner.run([docker,'start','--attach',name], ROOT, log, remaining)
        if result['status']=='ok':
            remaining = timeout-(time.monotonic()-started)
            if remaining <= 0: return {'status':'timeout'}
            state = control(docker,['inspect','--format','{{json .State}}',name],
                            timeout=min(15,remaining))
            info = json.loads(state.stdout) if state.returncode==0 else {}
            if info.get('Running') or info.get('ExitCode') != 0:
                result = {'status':'crash'}
        if result['status']=='ok' and output:
            remaining = timeout-(time.monotonic()-started)
            if remaining <= 0: return {'status':'timeout'}
            result = copy_output(docker,name,output[0],output[1],remaining)
        if result['status']=='ok' and snapshot:
            remaining = timeout-(time.monotonic()-started)
            if remaining <= 0: return {'status':'timeout'}
            committed = control(docker,['commit',name,snapshot],timeout=remaining)
            if committed.returncode: result={'status':'snapshot_error'}
        return result
    except subprocess.TimeoutExpired:
        return {'status':'timeout'}
    except (OSError, ValueError):
        return {'status':'launch_error'}
    finally:
        removed = control(docker,['rm','--force','--volumes',name],timeout=30)
        if removed.returncode and 'No such container' not in removed.stderr:
            raise RuntimeError('Could not remove grading container '+name+': '+removed.stderr)


def build_project(docker,image,snapshot,timeout,log,network='default',env=()):
    return container_job(docker,image,['./run_build.sh'],timeout,log,snapshot=snapshot,
                         network='bridge' if network=='default' else network,env=env)


def run_container(docker,image,source,output_dir,case,timeout,log):
    target='/input/'+source.name
    return container_job(docker,image,['./run_test.sh',target,'/output/result.txt'],timeout,log,
        uploads=[(source.resolve(),target)],
        env=['SA_PROFILE='+case['profile']],
        output=('/output/result.txt',output_dir/'result.txt'))


def evaluate(source, actual, output, java, javac):
    item = runner.load_case(source)
    output.mkdir(parents=True,exist_ok=True)
    inputs = output/'input'
    runner.prepare(item,inputs,javac,java)
    score = runner.assess_output({'status':'ok'},actual,item,inputs,output,java)
    (output/'score.txt').write_text(str(score),encoding='utf-8')
    return 0


def score_container(args, image, item, output, dest, temporary):
    source=item[2][0]
    case_path=('bonus/' if item[1].get('bonus') else '')+item[1]['profile']
    folder=temporary/item[1]['id']/case_path
    folder.mkdir(parents=True)
    shutil.copy2(source,folder/source.name)
    shutil.copy2(source.with_suffix('.out'),folder/source.with_suffix('.out').name)
    uploads=[(folder.parent,'/cases/bonus')] if item[1].get('bonus') else [(folder,'/cases/'+case_path)]
    if output.is_file(): uploads.append((output,'/output/result.txt'))
    result=container_job(args.docker,image,
        ['-c','exec python3 /workspace/grader/grade_submission.py "$@"','judge',
         '--evaluate','/cases/'+case_path+'/'+source.name,
         '--actual','/output/result.txt','--output','/evaluation',
         '--java',args.java,'--javac',args.javac],
        90+20*len(item[1]['queries']),dest/'judge.log',uploads=uploads,
        output=('/evaluation/score.txt',dest/'score.txt'))
    if result['status']!='ok' or not (dest/'score.txt').is_file():
        raise ValueError('judging failed')
    score=float((dest/'score.txt').read_text())
    if not 0 <= score <= 2: raise ValueError('invalid score')
    return score


def selected_cases(args):
    items = runner.cases(args.cases, args.profile)
    if args.include_hidden and args.profile in {'a5', 'all'}:
        items += runner.cases(ROOT/'hidden', 'a5')
    unique = {item[1]['id']:item for item in items}
    return sorted(unique.values(), key=lambda item:(item[1]['profile'], bool(item[1].get('bonus')),
                                                     not item[1].get('forward_only',False), item[1]['id']))


def grade(args):
    submission=args.submission.resolve()
    dockerfile=args.dockerfile.resolve()
    if not dockerfile.is_file() or not all((submission/name).is_file() for name in ('run_build.sh','run_test.sh')):
        raise ValueError('Dockerfile, run_build.sh and run_test.sh are required')
    items=selected_cases(args)
    if args.output.is_symlink(): raise ValueError('linked report directory')
    home=args.output.resolve()
    if (home == submission or home in submission.parents or below(home,submission)
            or home == ROOT.parent or home in ROOT.parent.parents
            or home == ROOT or home in dockerfile.parents
            or any(below(home,ROOT/name) for name in ('script','benchmark','hidden'))):
        raise ValueError('report directory overlaps project inputs')
    for item in items:
        if any(below(source.resolve(),home) for source in item[2]):
            raise ValueError('report directory contains test inputs')
    if home.is_symlink(): raise ValueError('linked report directory')
    if home.exists():
        if home.is_dir(): shutil.rmtree(home)
        else: home.unlink()
    home.mkdir(parents=True,exist_ok=False)
    image='sa-submission:'+uuid.uuid4().hex
    snapshot='sa-built:'+uuid.uuid4().hex
    records=[]
    try:
        with tempfile.TemporaryDirectory(prefix='sa-grade-') as directory:
            temporary=Path(directory)
            context=temporary/'context'
            copy_context(submission,dockerfile,context)
            command=[args.docker,'build','--network',args.build_network]
            for value in args.build_arg: command.extend(['--build-arg',value])
            built=runner.run([*command,'--tag',image,str(context)],ROOT,home/'image.log',args.build_timeout)
            if built['status']=='ok':
                built=build_project(args.docker,image,snapshot,args.build_timeout,home/'build.log',args.build_network,args.build_env)
            for item in items:
                _,case,_,_,_=item
                dest=home/case['id'];dest.mkdir()
                output=dest/'output';output.mkdir()
                execution={'status':built['status']}
                score=0.0
                if built['status']=='ok':
                    execution=run_container(args.docker,snapshot,item[2][0],output,case,
                                            args.timeout,dest/'container.log')
                    if execution['status']=='ok':
                        score=score_container(args,args.judge_image or image,item,output/'result.txt',dest,temporary)
                records.append({'case':case['id'],'score':score,'max_score':2,
                                'status':execution['status'],'bonus':case.get('bonus',False)})
                print(f'{case["id"]}: {score:.6f}/2',flush=True)
                report=runner.case_io.judge_analysis.summarize(records, [item[1] for item in items])
                (home/'report.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
            print(f'SCORE {report["score"]:.6f}/{report["max_score"]}',flush=True)
            return 0 if report['score']==report['total_max_score'] else 1
    finally:
        control(args.docker,['image','rm','--force',snapshot,image],timeout=30)


def main():
    parser=argparse.ArgumentParser(description=__doc__)
    mode=parser.add_mutually_exclusive_group(required=True)
    mode.add_argument('--submission',type=Path)
    mode.add_argument('--evaluate',type=Path,help=argparse.SUPPRESS)
    parser.add_argument('--actual',type=Path,help=argparse.SUPPRESS)
    parser.add_argument('--dockerfile',type=Path,default=ROOT.parent/'Dockerfile')
    parser.add_argument('--cases',type=Path,default=ROOT.parent/'handout/tests')
    parser.add_argument('--include-hidden',action='store_true',help='include the instructor A5 regular and bonus cases')
    parser.add_argument('--profile',choices=['all',*runner.PROFILES],default='all')
    parser.add_argument('--output',type=Path,required=True,help='report directory; existing contents are replaced')
    parser.add_argument('--docker',default='docker')
    parser.add_argument('--judge-image',help='separate trusted image used only to compile tests and judge outputs')
    parser.add_argument('--build-arg',action='append',default=[],help='Docker build argument, e.g. a build-only proxy')
    parser.add_argument('--build-env',action='append',default=[],help='environment passed only to run_build.sh')
    parser.add_argument('--java',default='java',help='Java command inside the grading container')
    parser.add_argument('--javac',default='javac',help='javac command inside the grading container')
    parser.add_argument('--build-network',default='default',help='Docker network for image and project builds')
    parser.add_argument('--build-timeout',type=float,default=900)
    parser.add_argument('--timeout',type=float,default=120,
                        help='external per-case deadline, excluding the one-time project build')
    args=parser.parse_args()
    if min(args.timeout,args.build_timeout)<=0: parser.error('timeouts must be positive')
    try:
        if args.evaluate: return evaluate(args.evaluate,args.actual,args.output,args.java,args.javac)
        return grade(args)
    except (OSError,ValueError,RuntimeError,subprocess.TimeoutExpired):
        print('ERROR')
        return 1


if __name__=='__main__':
    raise SystemExit(main())
