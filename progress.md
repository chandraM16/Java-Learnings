# Learning Progress

_Last updated: 2026-09-17 (Session 2)_

Status scale: Not Started → Introduced → Practiced → Comfortable → Mastered

## Current Phase
Phase 0 — Environment & Toolchain

## Current Topic
0.1 Lesson 1 review done → **awaiting answers to re-check questions** before starting Lesson 2.

## Topic Status

### Phase 0 — Environment
| Topic | Status | Evidence |
|---|---|---|
| 0.1 Java, JDK/JRE/JVM, compilation pipeline | Practiced | Compiled & ran by hand; correct on JIT warm-up; couldn't yet explain why `.class` runs without source |
| 0.2 Install JDK, `java`/`javac` | Practiced | Temurin 25.0.4.1 installed, JAVA_HOME in ~/.zshrc (verified 2026-09-17) |
| 0.2b JShell | Not Started | |
| 0.3 IntelliJ IDEA | Not Started | |
| 0.4 Maven quick start | Not Started | |

### Phases 1–19
Not Started (see `roadmap.md`).

## Needs Review
- **Compile-time vs run-time boundary** — gets the obvious cases, but needs to articulate *why* (static info vs actual values).
- **Reading error messages precisely** — Ex 3a: reported "file not found" (ran `javac Hello.java` after renaming) instead of the intended error; didn't notice the mismatch.
- **Artifacts vs source** — didn't know why `.class` runs without `.java`, or why `java Hello.java` leaves no `.class`.
- **Runtime type safety (Java vs TS)**, **JDK vs JRE in production**, **PATH vs JAVA_HOME** — quiz Q1/Q3/Q4 unanswered; taught in review, re-check pending.

## Exercises
| Date | Exercise | Result | Notes |
|---|---|---|---|
| 2026-09-17 | L1 Ex1 Install JDK | ✅ | |
| 2026-09-17 | L1 Ex2 Compile & run | ✅ steps / ⚠️ explanations | Parts 3 & 4 observed correctly, reasons unknown |
| 2026-09-17 | L1 Ex3 Break it | 2.5/3 | a) wrong command → wrong error; b) ✅; c) ✅ answer, no reasoning |
| 2026-09-17 | L1 Challenge (bytecode trade-offs) | Partial | Portability only |
| 2026-09-17 | L1 Quiz | 1/4 | Q2 ✅ (JIT warm-up) |

## Assessments
- None yet.

## Projects
- None yet.

## Confidence (mentor estimate)
- Java overall: 0.5/5
- Phase 0 concepts: 2/5

## Next
1. Learner answers the 4 re-check questions from the Lesson 1 review.
2. Lesson 2: JShell + anatomy of a Java program (classes, `main`, `static`, statements, packages basics, naming conventions).
3. Lesson 3: Variables & primitive types.
