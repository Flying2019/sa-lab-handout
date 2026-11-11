"""Lab1 answer bounds and whole-case correctness."""
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


def point_bounds(value, case):
    tokens = value.split()
    plain = [token[1:-1] if token.startswith('(') and token.endswith(')') else token
             for token in tokens]
    if any(not token for token in plain): raise ValueError('WRONG')
    upper = point_set(' '.join(plain), case)
    lower = point_set(' '.join(token for token in tokens if not token.startswith('(')), case)
    return lower, upper


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
        else: point_bounds(value, case)


def ordinary_score(actual, expected, case, **unused):
    """Normal execution is one point; missing/invalid answers lose the bonus."""
    try:
        validate_expected(expected, case)
        values = normalize(actual, case)
        for key, value in values.items():
            if case['queries'][key] == 'sign':
                low, high = bounds(expected[key])
                if not low <= value <= high: return 1.0
            else:
                lower, upper = point_bounds(expected[key], case)
                if not lower <= value <= upper: return 1.0
        return 2.0
    except (ValueError, TypeError, KeyError):
        return 1.0


def check(actual, expected, case, **kwargs):
    return score(actual, expected, case, **kwargs) == 2.0


def score(actual, expected, case, **kwargs):
    value = ordinary_score(actual, expected, case, **kwargs)
    return 0.0 if case.get("bonus") and value != 2.0 else value


def summarize(results, cases):
    regular_max = 2 * sum(not case.get('bonus') for case in cases)
    bonus_max = 2 * sum(bool(case.get('bonus')) for case in cases)
    regular = sum(row['score'] for row in results if not row.get('bonus'))
    bonus = sum(row['score'] for row in results if row.get('bonus'))
    return {'score':regular + 1.5 * bonus,
            'max_score':regular_max, 'total_max_score':regular_max+1.5 * bonus_max,
            'regular_score':regular, 'bonus_score':bonus,
            'bonus_lab_score':bonus / 2,
            'regular_max_score':regular_max, 'bonus_max_score':bonus_max,
            'results':results}
