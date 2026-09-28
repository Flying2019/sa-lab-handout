"""Read course markers from the restricted test sources."""
import re

def markers(files):
    """Authoring check for the documented marker spelling, NOT a Java analyzer."""
    queries, allocations = {}, []
    locations = {}
    # Preserve newlines to report useful source positions. Java 8 test subset, no text blocks.
    token = re.compile(r'//[^\n]*|/\*[\s\S]*?\*/|"(?:\\.|[^"\\])*"|\'(?:\\.|[^\'\\])*\'')
    for file in files:
        text = file.read_text(encoding="utf-8")
        text = token.sub(lambda m: "".join("\n" if c == "\n" else " " for c in m[0]), text)
        if re.search(r"\bBenchmarkN\b", text):
            raise ValueError(f"{file}: use unified Benchmark")
        if re.search(r"\bBenchmark\s*\.\s*testConst\s*\(", text):
            raise ValueError(f"{file}: testConst retired; use testSign")
        calls = list(re.finditer(r"\bBenchmark\s*\.\s*(alloc|testSign|test|reach)\s*\(", text))
        for call in calls:
            m = re.match(r"\s*([1-9][0-9]*)\s*([,)])", text[call.end():])
            if not m:
                raise ValueError(f"{file}: marker requires positive literal ID")
            key, delimiter = m.groups()
            kind = call[1]
            if delimiter != (")" if kind in {"alloc", "reach"} else ","):
                raise ValueError(f"{file}: invalid marker arguments")
            if kind == "alloc":
                if int(key) in allocations:
                    raise ValueError(f"duplicate allocation ID {key}")
                tail = text[call.end() + m.end():]
                # The restricted authoring syntax places one allocation in the
                # next simple statement. This is a lint check, not Java parsing.
                statement = re.match(r"\s*;\s*([^;]+);", tail)
                if not statement or len(re.findall(r"\bnew\b", statement[1])) != 1:
                    raise ValueError(f"{file}: alloc must precede one simple new statement")
                if re.search(r"\b(if|for|while|switch|return)\b|[{}]", statement[1]):
                    raise ValueError(f"{file}: ambiguous allocation marker")
                allocations.append(int(key))
            else:
                if key in queries:
                    raise ValueError(f"duplicate query ID {key}")
                queries[key] = "reach" if kind == "reach" else "sign" if kind == "testSign" else "points"
                locations[key] = {"file": file.as_posix(), "line": text[:call.start()].count("\n") + 1}
    return queries, sorted(allocations), locations
