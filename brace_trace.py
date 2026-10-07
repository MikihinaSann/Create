import re, sys

def clean(s):
    s = re.sub(r'//.*', '', s)
    s = re.sub(r'/\*.*?\*/', '', s, flags=re.S)
    s = re.sub(r'"([^"\\]|\\.)*"', '', s)
    s = re.sub(r"'([^'\\]|\\.)*'", '', s)
    return s

def trace(p):
    s = clean(open(p, encoding='utf8', errors='replace').read())
    d = 0
    ln = 1
    zeros = []
    negs = []
    for c in s:
        if c == '\n':
            ln += 1
        elif c == '{':
            d += 1
        elif c == '}':
            d -= 1
            if d == 0:
                zeros.append(ln)
            if d < 0:
                negs.append(ln)
                d = 0  # resync
    print(p, '| zeros:', zeros[-10:], '| negs:', negs, '| end', d)

for p in sys.argv[1:]:
    trace(p)
