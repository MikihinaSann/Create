import os, re, sys

ROOT = sys.argv[1] if len(sys.argv) > 1 else 'src/main/java'

bad = []
for root, dirs, files in os.walk(ROOT):
    for fn in files:
        if not fn.endswith('.java'):
            continue
        p = os.path.join(root, fn)
        s = open(p, encoding='utf8', errors='replace').read()
        s2 = re.sub(r'//.*', '', s)
        s2 = re.sub(r'/\*.*?\*/', '', s2, flags=re.S)
        s2 = re.sub(r'"([^"\\]|\\.)*"', '', s2)
        s2 = re.sub(r"'([^'\\]|\\.)*'", '', s2)
        d = 0
        bad_line = None
        ln = 1
        for c in s2:
            if c == '\n':
                ln += 1
            elif c == '{':
                d += 1
            elif c == '}':
                d -= 1
                if d < 0 and bad_line is None:
                    bad_line = ln
        if d != 0 or bad_line:
            bad.append((p, bad_line, d))

for p, l, d in bad:
    print(f'{d:+d} neg@{l} {p}')
print(len(bad), 'unbalanced files')
