"""Trusted source preparation, text parsing and scoring."""
from pathlib import Path
import re
from . import cases as case_io
from .runtime import run

PROFILES = case_io.PROFILES
prepare = case_io.prepare


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


def assess_output(execution, output, item, inputs, home, java):
    if execution['status'] != 'ok': return 0.0
    try:
        output = Path(output)
        if output.is_symlink() or not output.is_file() or output.stat().st_size > 2_000_000:
            return 1.0
        rows = parse_output(output.read_text(encoding='utf-8'))
        _, case, _, _, expected = item
        return case_io.checker(case['profile']).score(rows, expected, case,
                                input_root=inputs, home=home, java=java)
    except (OSError, ValueError, KeyError):
        return 1.0
