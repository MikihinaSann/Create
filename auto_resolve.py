import re, sys, pathlib

FABRIC_RE = re.compile(r'(net\.fabricmc|io\.github\.fabricators_of_create|infrastructure\.fabric|javax\.annotation|net\.createmod)')
NEOFORGE_RE = re.compile(r'(net\.neoforged|org\.jetbrains)')

def hunks(path):
    lines = pathlib.Path(path).read_text(encoding='utf-8', errors='replace').splitlines(keepends=True)
    out, i = [], 0
    while i < len(lines):
        if lines[i].startswith('<<<<<<<'):
            j = i + 1
            ours = []
            while not lines[j].startswith('======='):
                ours.append(lines[j]); j += 1
            j += 1; theirs = []
            while not lines[j].startswith('>>>>>>>'):
                theirs.append(lines[j]); j += 1
            out.append((i, j, ''.join(ours), ''.join(theirs)))
            i = j + 1
        else:
            i += 1
    return lines, out

def is_import_hunk(text):
    body = [l for l in text.splitlines() if l.strip()]
    return all(l.strip().startswith('import ') or l.strip().startswith('//') for l in body)

def resolve(path, dry=False):
    lines, hs = hunks(path)
    decisions = []
    for idx, (s, e, ours, theirs) in enumerate(hs):
        o_fab, t_fab = bool(FABRIC_RE.search(ours)), bool(FABRIC_RE.search(theirs))
        o_neo, t_neo = bool(NEOFORGE_RE.search(ours)), bool(NEOFORGE_RE.search(theirs))
        only_imports = is_import_hunk(ours) and is_import_hunk(theirs)
        d = None
        if only_imports:
            if o_fab and not t_fab: d = 'o'
            elif t_fab and not o_fab: d = 't'
            elif ours.strip() == '' and theirs.strip() != '': d = 't'
            elif theirs.strip() == '' and ours.strip() != '': d = 'o'
        else:
            # body hunk: only auto-pick if ours empty/theirs content or vice versa is skipped (ambiguous)
            if o_fab and not t_fab and '@Environment' in ours: d = 'o'
        decisions.append(d)
    if dry:
        return decisions, hs
    # apply non-None decisions bottom-up
    for idx in range(len(hs) - 1, -1, -1):
        d = decisions[idx]
        if d is None: continue
        s, e, ours, theirs = hs[idx]
        lines[s:e+1] = [ours if d == 'o' else theirs]
    pathlib.Path(path).write_text(''.join(lines), encoding='utf-8')
    return decisions

if __name__ == '__main__':
    files = pathlib.Path('src/main/java').rglob('*.java')
    auto, manual = [], []
    for f in files:
        txt = f.read_text(encoding='utf-8', errors='replace')
        if '<<<<<<<' not in txt: continue
        ds = resolve(str(f))
        if all(d is not None for d in ds): auto.append(str(f))
        else: manual.append((str(f), sum(1 for d in ds if d is None)))
    print(f"AUTO-RESOLVED: {len(auto)}")
    print(f"MANUAL: {len(manual)}")
    for m, n in sorted(manual): print(f"  {n} hunks  {m}")
