import re, sys

p = sys.argv[1]
start = int(sys.argv[2]) if len(sys.argv) > 2 else 1
end = int(sys.argv[3]) if len(sys.argv) > 3 else 10**9

lines = open(p, encoding='utf8', errors='replace').read().split('\n')
d = 0
for i, l in enumerate(lines, 1):
    t = re.sub(r'//.*', '', l)
    t = re.sub(r'"([^"\\]|\\.)*"', '', t)
    t = re.sub(r"'([^'\\]|\\.)*'", '', t)
    o = t.count('{'); c = t.count('}')
    nd = d + o - c
    if (o or c) and start <= i <= end:
        print(i, 'd', d, '->', nd, '|', l.rstrip()[:80])
    d = nd
