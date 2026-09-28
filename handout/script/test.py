#!/usr/bin/env python3
"""Uniform launcher. Output parsing is only ID: string; judges own semantics."""
import argparse
import json
import os
from pathlib import Path
import re
import sys

sys.path.insert(0, str(Path(__file__).resolve().parent))
import cases as case_io
from runtime import run

ROOT = case_io.ROOT
PROFILES = case_io.PROFILES
prepare = case_io.prepare
workspace = case_io.workspace


def parse_output(text):
    rows = {}
    for line in text.splitlines():
        if not line.strip(): continue
        match = re.fullmatch(r'\s*([1-9][0-9]*)\s*:\s*(.*?)\s*', line)
        if not match or match[1] in rows: raise ValueError('ERROR')
        rows[match[1]] = match[2]
    return rows


def load_case(path):
    return case_io.load_case(path, parse_output)


def cases(root, task='all'):
    return case_io.cases(root, task, parse_output)


def validate(args):
    home = workspace('validate') if args.compile else None
    ok = True
    for item in cases(args.cases, args.task):
        passed = True
        try:
            if home: prepare(item, home/item[1]['id'], args.javac, args.java)
        except (OSError, ValueError): passed = False
        print('PASS' if passed else 'ERROR', item[1]['id'], flush=True)
        ok &= passed
    return int(not ok)


def judge(args):
    home = workspace('judge')
    results = []
    for item in cases(args.cases, args.task):
        _, case, _, _, expected = item
        case_home = home/case['id']; case_home.mkdir()
        inputs, output = case_home/'input', case_home/'result.txt'
        passed = False
        points = 0.0
        try:
            prepare(item, inputs, args.javac, args.java)
            if args.jar:
                command = [args.java,'-Djava.library.path='+str(args.jar.resolve().parent/'smt-native'),
                           '-jar',str(args.jar.resolve()),'-a',
                           'pku-se' if case['profile'].startswith('b') else 'pku-course',
                           '--output-dir',str(case_home/'tai-e'),'-cp',str(inputs/'classes'),'-m','PkuEntry']
            else:
                values = {'input':str(item[2][0]),'prepared':str(inputs),'output':str(output),'entry':case['entry'],
                          'classpath':str(inputs/'classes'),'task':case['profile'],
                          'profile':case['profile'],'python':sys.executable}
                command = [values[t[1:-1]] if t.startswith('{') and t.endswith('}') else t for t in args.command]
            env={'SA_INPUT':str(inputs),'SA_OUTPUT':str(output),'SA_TASK':case['profile'],
                 'SA_PROFILE':case['profile'],'SA_CONTEXT':case.get('context','')}
            saved={key:os.environ.get(key) for key in env}
            try:
                os.environ.update(env)
                execution=run(command,Path.cwd(),case_home/'tool.log',args.timeout,output)
            finally:
                for key,value in saved.items():
                    if value is None: os.environ.pop(key,None)
                    else: os.environ[key]=value
            points = assess_output(execution, output, item, inputs, case_home, args.java)
            passed = points == 2.0
        except (OSError, ValueError, KeyError):
            pass
        results.append({'case':case['id'],'status':'pass' if passed else 'error','score':points,'max_score':2})
        print('PASS' if passed else 'ERROR',case['id'],f'{points:.6f}/2',flush=True)
    report={'results':results,'passed':sum(r['status']=='pass' for r in results),'total':len(results),'score':sum(r['score'] for r in results),'max_score':2*len(results)}
    (home/'report.json').write_text(json.dumps(report,indent=2)+'\n',encoding='utf-8')
    print(f"{report['passed']}/{report['total']} SCORE {report['score']:.6f}/{report['max_score']}")
    return int(report['passed']!=report['total'])


def assess_output(execution, output, item, inputs, home, java):
    if execution['status'] != 'ok': return 0.0
    try:
        output=Path(output)
        if output.is_symlink() or not output.is_file() or output.stat().st_size>2_000_000:
            return 1.0
        rows=parse_output(output.read_text(encoding='utf-8'))
        _,case,_,_,expected=item
        return case_io.checker(case['profile']).score(rows,expected,case,
                                input_root=inputs,home=home,java=java)
    except (OSError,ValueError,KeyError):
        return 1.0


def analyze(args):
    """Analyze one Java source file without reading reference answers."""
    source=args.input.resolve()
    profile=args.task if args.task!='all' else os.environ.get('SA_PROFILE') or source.parent.name
    item=case_io.source_case(source,profile)
    inputs=workspace('analyze')/'input'
    case=prepare(item,inputs,args.javac,args.java)
    output=args.output.resolve()
    output.parent.mkdir(parents=True,exist_ok=True)
    output.unlink(missing_ok=True)
    os.environ.update(SA_INPUT=str(inputs),SA_OUTPUT=str(output),
                      SA_PROFILE=case['profile'],SA_TASK=case['profile'],
                      SA_CONTEXT=case.get('context',''))
    if args.jar:
        command=[args.java,'-Djava.library.path='+str(args.jar.resolve().parent/'smt-native'),
                 '-jar',str(args.jar.resolve()),'-a',
                 'pku-se' if case['profile'].startswith('b') else 'pku-course',
                 '--output-dir',str(output.parent/'tai-e'),'-cp',str(inputs/'classes'),'-m','PkuEntry']
    else:
        values={'input':str(source),'prepared':str(inputs),'output':str(output),'classpath':str(inputs/'classes'),
                'entry':case['entry'],'profile':case['profile'],'task':case['profile'],'python':sys.executable}
        command=[values[t[1:-1]] if t.startswith('{') and t.endswith('}') else t for t in args.command]
    result=run(command,Path.cwd(),output.parent/'tool.log',args.timeout,output)
    return 0 if result['status']=='ok' else 1


def main(argv=None):
    parser=argparse.ArgumentParser(description=__doc__)
    sub=parser.add_subparsers(dest='action',required=True)
    for action in ('validate','judge','analyze'):
        p=sub.add_parser(action)
        if action=='analyze':
            p.add_argument('--input',type=Path,required=True)
            p.add_argument('--output',type=Path,required=True)
        p.add_argument('--cases',type=Path,default=ROOT/'tests')
        p.add_argument('--profile','--task',dest='task',choices=['all',*PROFILES],default='all')
        p.add_argument('--java',default='java'); p.add_argument('--javac',default='javac')
        if action=='validate': p.add_argument('--compile',action='store_true')
        else:
            p.add_argument('--timeout',type=float,default=30)
            group=p.add_mutually_exclusive_group(required=True)
            group.add_argument('--jar',type=Path)
            group.add_argument('--command',nargs=argparse.REMAINDER)
    args=parser.parse_args(argv)
    try:
        if getattr(args,'timeout',1)<=0: raise ValueError('ERROR')
        if args.action!='validate' and not args.jar and not args.command: raise ValueError('ERROR')
        return {'validate':validate,'judge':judge,'analyze':analyze}[args.action](args)
    except (OSError,ValueError,KeyError):
        print('ERROR')
        return 1


if __name__=='__main__': sys.exit(main())
