#!/usr/bin/env python3
"""Merge-conflict hunk tool.
Usage:
  py resolve_hunks.py list <file>           -> print numbered hunks (truncated)
  py resolve_hunks.py resolve <file> <spec> -> spec like "0:o,1:t,2:u" (o=ours t=theirs u=union o+t)
  py resolve_hunks.py custom <file> <idx> <replacement-file>
"""
import sys, re

def parse_hunks(text):
    lines = text.split('\n')
    hunks = []          # list of dicts: start,end,ours,theirs
    i = 0
    while i < len(lines):
        if lines[i].startswith('<<<<<<<'):
            start = i
            ours, theirs = [], []
            cur = ours
            i += 1
            while i < len(lines) and not lines[i].startswith('>>>>>>>'):
                if lines[i].startswith('======='):
                    cur = theirs
                elif lines[i].startswith('|||||||'):
                    cur = []  # base section, ignore
                else:
                    cur.append(lines[i])
                i += 1
            hunks.append(dict(start=start, end=i, ours=ours, theirs=theirs))
        i += 1
    return lines, hunks

def cmd_list(path):
    lines, hunks = parse_hunks(open(path, encoding='utf-8', errors='replace').read())
    for n, h in enumerate(hunks):
        print(f"--- hunk {n} (lines {h['start']}-{h['end']}) ---")
        print("OURS:"); [print("  " + l) for l in h['ours']]
        print("THEIRS:"); [print("  " + l) for l in h['theirs']]

def apply(path, choices):
    lines, hunks = parse_hunks(open(path, encoding='utf-8', errors='replace').read())
    # apply from last to first so indexes stay valid
    for idx in sorted(range(len(hunks)), reverse=True):
        if idx not in choices:
            print(f"warning: hunk {idx} unresolved in {path}", file=sys.stderr)
            continue
        h = hunks[idx]
        c = choices[idx]
        if c == 'o': rep = h['ours']
        elif c == 't': rep = h['theirs']
        elif c == 'u': rep = h['ours'] + h['theirs']
        elif c == 'i': rep = h['theirs'] + h['ours']  # inverse union
        elif isinstance(c, list): rep = c
        else: raise ValueError(c)
        lines[h['start']:h['end'] + 1] = rep
    open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(lines))

if __name__ == '__main__':
    if sys.argv[1] == 'list':
        cmd_list(sys.argv[2])
    elif sys.argv[1] == 'resolve':
        path = sys.argv[2]
        choices = {}
        for part in sys.argv[3].split(','):
            i, c = part.split(':')
            choices[int(i)] = c
        apply(path, choices)
    elif sys.argv[1] == 'custom':
        path, idx, rep = sys.argv[2], int(sys.argv[3]), sys.argv[4]
        apply(path, {idx: open(rep, encoding='utf-8').read().split('\n')})
