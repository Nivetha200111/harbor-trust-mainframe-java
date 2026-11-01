# Build report: FAIL

Whole-repository build check by Diocle (`contracts/build-report.schema.json` v0.1). Each program is certified on its own; this checks them together.

## Why

- htdormsw...:71: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- htdormsw...:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- htdormsw...:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- htfxconv...:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- htovdfee...:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING

## Programs compiled together

| Program | Java files | Classes | Alone |
|---|---|---|---|
| HTCBR | 1 | Htcbr | ok |
| HTCHK | 1 | Htchk | ok |
| HTCHQCLR | 1 | Htchqclr | ok |
| HTCTRLBR | 1 | Htctrlbr | ok |
| HTDBDEF | 1 | Htdbdef | ok |
| HTDOR | 1 | Htdor | ok |
| HTDORMSW | 1 | Htdormsw | ok |
| HTFXC | 1 | Htfxc | ok |
| HTFXCONV | 1 | Htfxconv | ok |
| HTGLR | 1 | Htglr | ok |
| HTGLRECO | 1 | Htglreco | ok |
| HTIACR | 1 | Htiacr | ok |
| HTLOANAM | 1 | Htloanam | ok |
| HTLON | 1 | Htlon | ok |
| HTNSF | 1 | Htnsf | ok |
| HTOVDFEE | 1 | Htovdfee | ok |
| HTSRT | 1 | Htsrt | ok |
| HTSTM | 1 | Htstm | ok |
| HTSTMTM | 1 | Htstmtm | ok |

All 19 source file(s) in one javac run: **ok** (exit 0)

## CALLs (26 static CALL statement(s) examined)

- **fail** htdormsw/src/HTDATE.cbl:71: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- **fail** htdormsw/src/HTAUDT.cbl:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- **fail** htdormsw/src/HTPOST.cbl:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- **fail** htfxconv/src/HTFXRT.cbl:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- **fail** htovdfee/src/HTFEEP.cbl:68: CALL 'HTAUDT' passes 1 argument?s? but HTAUDT expects 3 in PROCEDURE DIVISION USING
- 5 informational note(s) (runtime routines, arguments that could not be compared) are in build-report.json

## Dependency graph

66 nodes, 97 edges (full graph in build-report.json).

## What was checked

- 19 program?s?: each compiled alone and all 19 Java file?s? compiled together with javac -proc:none
- top-level class names across programs
- 12 copybook?s? for drift between files ?text and analyzer layout?
- 26 static CALL statement?s?: target present, USING count and argument lengths where both sides are plain items

## What was NOT checked

- behaviour of the programs together: each program is verified on its own by differential testing against the original COBOL
- that the programs' files and datasets line up at run time ?JCL step order, DD wiring?
- CALL USING arguments that are subscripted, computed, BY VALUE or outside WORKING-STORAGE/LINKAGE items
- dynamic CALLs and programs outside this repository
