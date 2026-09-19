# Learning Progress

_Last updated: 2026-09-18 (Session 3)_

Status scale: Not Started → Introduced → Practiced → Comfortable → Mastered

## Current Phase
Phase 1 — Java Fundamentals (Phase 0 items 0.3 IntelliJ / 0.4 Maven deferred until just before Project 1)

## Current Topic
Lesson 3 — Variables & primitive types (delivered 2026-09-18, awaiting exercises). Lesson 2 closed (Challenge + Quiz Q1–3 skipped).

Note: learner chose to move on without answering the Lesson 1 re-check questions. Those ideas (compile vs launch vs run time; artifacts vs source) are folded into Lesson 2 Ex3 + Quiz as spaced revision — verify there.

## Topic Status

### Phase 0 — Environment
| Topic | Status | Evidence |
|---|---|---|
| 0.1 Java, JDK/JRE/JVM, compilation pipeline | Practiced | Compiled & ran by hand; correct on JIT warm-up; couldn't yet explain why `.class` runs without source |
| 0.2 Install JDK, `java`/`javac` | Practiced | Temurin 25.0.4.1 installed, JAVA_HOME in ~/.zshrc (verified 2026-09-17) |
| 0.2b JShell | Practiced | L2 Ex1: 7/7 predictions correct |
| 0.3 IntelliJ IDEA | Not Started | |
| 0.4 Maven quick start | Not Started | |

### Phase 1 — Fundamentals
| Topic | Status | Evidence |
|---|---|---|
| 1.1 Anatomy: class, `main`, statements, comments, packages, imports, naming | Practiced | L2 Ex2: packaged build/run correct; explained classpath + FQN errors mostly correctly (named NoClassDefFoundError as generic mismatch) |
| 1.2 Variables, primitives, `var`, casting, overflow | Introduced | Lesson 3 delivered |
| 1.3–1.7 | Not Started | |

### Phases 2–19
Not Started (see `roadmap.md`).

## Needs Review
- **Compile-time vs run-time boundary** — improving: L2 Ex3 correctly identified a launch-time error and why. Still to show: *why javac can't catch* launch-time issues (entry point = launcher convention, not a language rule). Lesson 3 adds a 4th category: silent wrong results (overflow, integer division).
- **Reading error messages precisely** — Ex 3a: reported "file not found" (ran `javac Hello.java` after renaming) instead of the intended error; didn't notice the mismatch.
- **Artifacts vs source** — didn't know why `.class` runs without `.java`, or why `java Hello.java` leaves no `.class`. (Improving: L2 Ex2 Q4 correctly reasoned the package name is stored inside the .class.)
- **Runtime type safety (Java vs TS)**, **JDK vs JRE in production**, **PATH vs JAVA_HOME** — quiz Q1/Q3/Q4 unanswered; taught in review; re-check skipped by learner → revisit later (PATH/JAVA_HOME when Maven is introduced in 0.4; JRE images in Phase 16; casts in Phase 2 polymorphism).

## Exercises
| Date | Exercise | Result | Notes |
|---|---|---|---|
| 2026-09-17 | L1 Ex1 Install JDK | ✅ | |
| 2026-09-17 | L1 Ex2 Compile & run | ✅ steps / ⚠️ explanations | Parts 3 & 4 observed correctly, reasons unknown |
| 2026-09-17 | L1 Ex3 Break it | 2.5/3 | a) wrong command → wrong error; b) ✅; c) ✅ answer, no reasoning |
| 2026-09-17 | L1 Challenge (bytecode trade-offs) | Partial | Portability only |
| 2026-09-17 | L1 Quiz | 1/4 | Q2 ✅ (JIT warm-up) |
| 2026-09-17 | L1 re-check follow-ups | Skipped | Learner moved to Lesson 2 |
| 2026-09-18 | L2 Ex1 JShell predictions | ✅ 7/7 | Didn't answer "which differ from JS / rule for 7/2" |
| 2026-09-18 | L2 Ex2 Packaged program | ✅ | Q3 reasoning correct (JVM looks for out/App.class). Q4 right idea (name baked into .class); error was NoClassDefFoundError, not ClassNotFoundException. Code for step 5 clean. |
| 2026-09-18 | L2 Ex3 Broken OrderSummary | ✅ code / ⚠️ report | Fixed all 6 bugs, correct 3 rounds, launch-time reasoning correct. Report listed only 3 of 6 problems; no pre-compile list. |
| 2026-09-18 | L2 Challenge + Quiz | Skipped | Q4 partly covered by Ex3 answer |
| — | L3 exercises (predictions, SubscriptionBilling, broken MetricsReport, type-choice challenge, quiz) | Pending | |

## Assessments
- None yet.

## Projects
- None yet.

## Confidence (mentor estimate)
- Java overall: 0.5/5
- Phase 0 concepts: 2/5
- Lesson 2 (packages/classpath): 3/5 — reasoning noticeably better than Lesson 1

## Next
1. Review Lesson 3 submissions (key signals: do they spot overflow in SubscriptionBilling unprompted? do they classify the "silently wrong" bugs?).
2. Lesson 4: Strings & reference types (`==` vs `equals`, immutability, `StringBuilder`, primitives vs references in memory).
3. Lesson 5: Operators & expressions (short-circuit, precedence, `++`, compound assignment).
