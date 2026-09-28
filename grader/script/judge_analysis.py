"""Lab1 bounds, soundness and the legacy precision bonus."""
SIGNS = {'+': {1}, '-': {-1}, '0': {0}, '+0': {0, 1},
         '-0': {-1, 0}, '+-0': {-1, 0, 1}}


def point_set(value, case):
    tokens = value.split()
    if any(not t.isascii() or not t.isdecimal() or t.startswith('0') for t in tokens):
        raise ValueError('WRONG')
    ids = [int(t) for t in tokens]
    if len(ids) != len(set(ids)) or not set(ids) <= set(case['allocations']):
        raise ValueError('WRONG')
    return set(ids)


def bounds(value):
    parts = value.split(' .. ')
    if len(parts) == 1: parts *= 2
    if len(parts) != 2 or any(part not in SIGNS for part in parts):
        raise ValueError('WRONG')
    low, high = (SIGNS[part] for part in parts)
    if not low <= high: raise ValueError('WRONG')
    return low, high


def normalize(rows, case):
    if rows.keys() != case['queries'].keys(): raise ValueError('WRONG')
    result = {}
    for key, value in rows.items():
        if case['queries'][key] == 'sign':
            if value not in SIGNS: raise ValueError('WRONG')
            result[key] = SIGNS[value]
        else:
            result[key] = point_set(value, case)
    return result


def validate_expected(rows, case):
    if rows.keys() != case['queries'].keys(): raise ValueError('WRONG')
    for key, value in rows.items():
        if case['queries'][key] == 'sign': bounds(value)
        else: point_set(value, case)


def score(actual, expected, case, **unused):
    """Normal execution is one point; missing/invalid answers lose the bonus."""
    try:
        validate_expected(expected, case)
        values = normalize(actual, case)
        precision = []
        for key, value in values.items():
            if case['queries'][key] == 'sign':
                low, high = bounds(expected[key])
                if not low <= value <= high: return 1.0
                precision.append(1.0)
            else:
                reference = point_set(expected[key], case)
                if not reference <= value: return 1.0
                precision.append((len(reference)+1)/(len(value)+1))
        return 1.0 + sum(precision)/len(precision)
    except (ValueError, TypeError, KeyError, ZeroDivisionError):
        return 1.0


def check(actual, expected, case, **kwargs):
    return score(actual, expected, case, **kwargs) == 2.0
