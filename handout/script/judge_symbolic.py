import re
from runtime import run


def validate_expected(rows, case):
    if rows.keys() != case['queries'].keys() or any(v not in {'reachable','unreachable'} for v in rows.values()):
        raise ValueError('ERROR')


def check(actual, expected, case, input_root, home, java='java'):
    try:
        validate_expected(expected, case)
        if actual.keys() != expected.keys(): return False
        # Validate every input before starting any application code.
        parsed = {key: value if value in {'unknown','unreachable'} else
                  parse_input(value, case['parameters'], case['profile']) for key,value in actual.items()}
        for key, value in parsed.items():
            if isinstance(value, str):
                if value != 'unreachable' or expected[key] != 'unreachable': return False
                continue
            args_file, result_file = home/f'replay-{key}.in', home/f'replay-{key}.txt'
            args_file.write_text(encode_input(value)+'\n', encoding='utf-8')
            execution = run([java, '-cp', str(input_root/'classes'), 'benchmark.internal.CourseReplay',
                             str(input_root/'classes'), case['entry'], str(args_file), str(result_file)],
                            home, home/f'replay-{key}.log', 15)
            if execution['status'] != 'ok' or not result_file.is_file(): return False
            line = result_file.read_text(encoding='utf-8').strip()
            if not re.fullmatch(r'normal:(?:[1-9][0-9]*(?: [1-9][0-9]*)*)?', line): return False
            if key not in line.split(':',1)[1].split() or expected[key] != 'reachable': return False
        return True
    except (ValueError, OSError, UnicodeError):
        return False

def score(actual, expected, case, **kwargs):
    return 2.0 if check(actual, expected, case, **kwargs) else 1.0


def domain(task):
    return {"int_min": -3 if task == "b3" else -16,
            "int_max": 3 if task == "b3" else 16,
            "array_max_length": 3, "element_min": -3, "element_max": 3}


def validate_parameters(task, types):
    if task not in {'b1','b2','b3'} or any(t not in {'int','int[]'} for t in types): raise ValueError('ERROR')
    if task == "b3":
        if types.count("int[]") > 1 or types.count("int") > 1: raise ValueError('ERROR')
    elif types.count("int") > 2 or "int[]" in types: raise ValueError('ERROR')


def parse_input(text, types, task):
    validate_parameters(task, types)
    if not types:
        if text != "()": raise ValueError("zero parameters require ()")
        return []
    values = []
    rest = text
    limits = domain(task)
    integer = r"-?(?:0|[1-9][0-9]*)"
    for i, kind in enumerate(types):
        if i:
            if not rest or not rest[0].isspace(): raise ValueError("parameters require whitespace")
            rest = rest.lstrip()
        pattern = r"\[[^\[\]]*\]" if kind == "int[]" else integer
        m = re.match(pattern, rest)
        if not m: raise ValueError("parameter type or syntax mismatch")
        token = m[0]; rest = rest[m.end():]
        if kind == "int[]":
            body = token[1:-1].strip()
            pieces = body.split(",") if body else []
            if any(not re.fullmatch(integer, x.strip()) for x in pieces):
                raise ValueError("array must contain decimal integers")
            value = [int(x) for x in pieces]
            if len(value) > 3 or any(x < -3 or x > 3 for x in value):
                raise ValueError("array outside input domain")
        else:
            value = int(token)
            if not limits["int_min"] <= value <= limits["int_max"]:
                raise ValueError("integer outside input domain")
        values.append(value)
    if rest.strip(): raise ValueError("extra parameters or trailing text")
    return values


def encode_input(values):
    def scalar(value):
        if isinstance(value, list): return "[" + ",".join(map(str, value)) + "]"
        return str(value)
    return "\t".join(map(scalar, values)) if values else "()"


