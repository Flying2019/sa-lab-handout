"""Source preparation; task-specific output semantics live in the judges."""
import hashlib
import json
from pathlib import Path
import re
import shutil
import tempfile
from markers import markers
from runtime import run
import judge_analysis
import judge_symbolic

ROOT = Path(__file__).resolve().parents[1]
PROFILES = ('a1','a2','a3','a4','a5','b1','b2','b3')


def checker(profile):
    return judge_symbolic if profile.startswith('b') else judge_analysis


def workspace(label):
    base = ROOT/'.work'
    base.mkdir(exist_ok=True)
    return Path(tempfile.mkdtemp(prefix=label+'-', dir=base))


def source_case(path, profile=None):
    path = Path(path).resolve()
    profile = profile or path.parent.name
    if profile not in PROFILES or not path.is_file() or path.suffix!='.java' or path.is_symlink():
        raise ValueError('ERROR')
    code = re.sub(r'/\*[\s\S]*?\*/|//[^\n]*', '', path.read_text(encoding='utf-8'))
    if not re.search(r'public\s+class\s+'+re.escape(path.stem)+r'\b', code): raise ValueError('ERROR')
    package = re.search(r'\bpackage\s+([\w.]+)\s*;', code)
    entry = (package[1]+'.' if package else '')+path.stem
    declarations = re.findall(r'public\s+static\s+(void|int)\s+'+re.escape(path.stem)+r'\s*\(([^)]*)\)',code)
    if len(declarations) != 1: raise ValueError('ERROR')
    return_type, parameters_text = declarations[0]
    parameters = []
    for parameter in parameters_text.split(',') if parameters_text.strip() else []:
        match = re.fullmatch(r'\s*(int\s*\[\s*\]|String\s*\[\s*\]|int)\s+[A-Za-z_$][\w$]*\s*', parameter)
        if not match: raise ValueError('ERROR')
        parameters.append(re.sub(r'\s','',match[1]))
    queries, allocations, locations = markers([path])
    if not queries: raise ValueError('ERROR')
    if profile in {'a1','a2'}:
        if (set(queries.values()) != {'sign'} or len(parameters)>3 or any(t!='int' for t in parameters)
            or return_type!='void' or re.search(r'\b(args|Object|String|new|extends|implements|interface|main)\b|\[|\]',code)):
            raise ValueError('ERROR')
    elif profile.startswith('b'):
        if set(queries.values()) != {'reach'} or allocations: raise ValueError('ERROR')
        judge_symbolic.validate_parameters(profile, parameters)
    elif parameters != ['String[]'] or return_type!='void' or 'reach' in queries.values():
        raise ValueError('ERROR')
    if profile in {'a3','a4'} and set(queries.values()) != {'points'}: raise ValueError('ERROR')
    case = {'id':profile+'-'+path.stem, 'task':profile, 'profile':profile, 'entry':entry,
            'entry_method':path.stem, 'parameters':parameters, 'launch_entry':'PkuEntry',
            'queries':queries, 'allocations':allocations}
    if profile=='a4':
        case['context'] = '1-call' if path.stem.endswith('1Call') else '1-object' if path.stem.endswith('1Object') else None
        if not case['context']: raise ValueError('ERROR')
    for location in locations.values(): location['file']=path.name
    return path.parent, case, [path], locations, {}


def load_case(path, parse):
    folder,case,files,locations,_ = source_case(path)
    answer=files[0].with_suffix('.out')
    if answer.is_symlink(): raise ValueError('ERROR')
    expected=parse(answer.read_text(encoding='utf-8'))
    checker(case['profile']).validate_expected(expected,case)
    return folder,case,files,locations,expected


def cases(root, profile, parse):
    root = Path(root)
    files = [root] if root.is_file() else sorted(root.rglob('*.java'))
    items = [load_case(p,parse) for p in files if profile=='all' or p.parent.name==profile]
    if not items or len({i[1]['id'] for i in items})!=len(items): raise ValueError('ERROR')
    return items


def prepare(item, dest, javac, java='java'):
    _, case, files, locations, _ = item
    dest.mkdir(parents=True)
    source, classes = dest/'src', dest/'classes'
    source.mkdir(); classes.mkdir()
    for file in files: shutil.copy2(file, source/file.name)
    sdk = source/'benchmark/internal/Benchmark.java'
    sdk.parent.mkdir(parents=True)
    shutil.copy2(ROOT/'benchmark/internal/Benchmark.java', sdk)
    if case['profile'].startswith('b'):
        shutil.copy2(ROOT/'script/CourseReplay.java', source/'CourseReplay.java')
    defaults={'int':'0','int[]':'new int[0]','String[]':'args'}
    wrapper=source/'PkuEntry.java'
    if wrapper.exists(): raise ValueError('ERROR')
    wrapper.write_text('public class PkuEntry { public static void main(String[] args) { '
                       +case['entry']+'.'+case['entry_method']+'('
                       +','.join(defaults[t] for t in case['parameters'])+'); }}',encoding='utf-8')
    sources=sorted(source.rglob('*.java'))
    argfile=dest/'javac.args'
    argfile.write_text('\n'.join('"'+p.as_posix()+'"' for p in sources),encoding='utf-8')
    execution=run([javac,'--release','17','-g','-encoding','UTF-8','-d',str(classes),'@'+str(argfile)],
                  dest,dest/'compile.log',60)
    if execution['status']!='ok': raise ValueError('ERROR')
    request=dict(case,source_root='src',classpath=['classes'],query_locations=locations,
                 source_hashes={p.relative_to(source).as_posix():hashlib.sha256(p.read_bytes()).hexdigest() for p in sources})
    if case['profile'] in {'a1','a2'}: request['parameter_signs']=['+-0']*len(case['parameters'])
    if case['profile'].startswith('b'):
        request['domain']=judge_symbolic.domain(case['profile'])
        request['bounds']={'precise_unroll':4, 'nested_loops':True, 'recursion':True}
        signature=dest/'parameters.txt'
        execution=run([java,'-cp',str(classes),'benchmark.internal.CourseReplay',str(classes),
                       case['entry'],'--describe',str(signature)],dest,dest/'signature.log',10)
        if execution['status']!='ok' or signature.read_text().split()!=case['parameters']: raise ValueError('ERROR')
    (dest/'input.json').write_text(json.dumps(request,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    return request
