# Learning Progress

_Last updated: 2026-09-19 (Session 4)_

Status scale: Not Started → Introduced → Practiced → Comfortable → Mastered

## Current Phase
Phase 1 — Java Fundamentals (Phase 0 items 0.3 IntelliJ / 0.4 Maven deferred until just before Project 1)

## Current Topic
Lesson 3 — Variables & primitive types. Ex1 reviewed (9/12). Ex2 v1 + v2 reviewed (2026-09-19) → reference solution shown. Predictions ✅, Challenge ✅, Quiz 2/4, Ex3 3/7 (predicted only, didn't run). **Awaiting: Ex3 actually compiled/run + fixed with output pasted; re-answer Quiz Q2 (var vs let) & Q3 (why cast only for narrowing).** Then Lesson 4. Lesson 2 closed (Challenge + Quiz Q1–3 skipped).

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
| 1.2 Variables, primitives, `var`, casting, overflow | Practiced | Rules known in isolation (predictions ✅, type choices 9/9); applying them in code still inconsistent (Ex3 3/7) |
| 1.3–1.7 | Not Started | |

### Phases 2–19
Not Started (see `roadmap.md`).

## Needs Review
- **Applying integer-division rule in own code (L3)** — knows the rule in isolation (Ex1 ✅) but wrote `a.intValue() * 100 / b.intValue()` in Ex2. Knowledge → application gap.
- **Choosing types by meaning** — v1 used BigDecimal for counts (fixed in v2 ✅). 'Smallest unit = source of truth' now explained correctly in own words (2026-09-19) → verify in future code.
- **Following explicit instructions** — skipped requested comments/questions in consecutive submissions.
- ~~Casting vs overflow~~ / ~~char is a number~~ — resolved 2026-09-19 (follow-up predictions 3/3).
- **Overflow is silent, never an exception (L3)** — labelled cacheTtlMs overflow as run time. Also: declaring the target `long` doesn't help; an operand must be long.
- **Converting a crash into a silent bug (L3)** — proposed making `/ by zero` a double division (→ Infinity). Teach: guard the zero case; a crash is better than a wrong number.
- **`var` (Java) vs `let`/`var` (JS)** — conflated with JS hoisting. Java `var` = compile-time inferred static type.
- **Actually running code** — predicted Ex3 without compiling/running it; answers would have self-corrected.
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
| 2026-09-19 | L3 Ex1 Predictions | 9/12 | ✅ int division, %, round, overflow, `L` fix. ❌ `(int) 9.99` → said overflow −10 (it truncates to 9). ❌ `10 / 0.0` → said 0.0 (Infinity). ❓ `'A'+2` right idea, no value; `(char)('A'+2)` idk. `-9/4` right value, rule unknown. |
| 2026-09-19 | L3 Ex2 SubscriptionBilling v1 | ⚠️ | Self-discovered `BigDecimal` + used String constructor (good). Bugs: ×1000 instead of ×100 for paise (silently wrong 10×); percentage via `intValue()` int division → 16 instead of 16.49 (exact lesson bug); counts stored as BigDecimal; part 2 (per-day avg) not attempted — didn't know how; no type-choice comments. |
| 2026-09-19 | L3 Ex2 SubscriptionBilling v2 | ✅ numbers / ⚠️ design | Fixed: ×100, int counts, percentage via `(double)` cast (correct placement). Regressed: `new BigDecimal(999.00)` (double ctor). Design: rupees→paise via `.longValue()` truncates any paise in price (₹999.50 would silently lose ₹1.25M/month). Didn't use skeleton. Output 8.325E7 (sci. notation). Type comments still missing (asked twice). Didn't answer estimate / "what would catch ×1000" questions. |
| 2026-09-19 | L3 Ex2 ₹999.50 task | ✅ | Correctly identified `longValue()` truncation + stated "calculate in smallest unit, convert for display" in own words. (Didn't paste totals.) |
| 2026-09-19 | L3 Ex2 "what catches ×1000" | ✅ partial | Proposed invariant test (paise/rupees == 100) — valid. Taught: example-based test with hand-computed expected value + named constant `PAISE_PER_RUPEE`. |
| 2026-09-19 | L3 follow-up predictions | ✅ 3/3 | Truncation vs overflow now correct; char→int correct. Gap closed. |
| 2026-09-19 | L3 Ex3 MetricsReport (prediction only) | 3/7 | ✅ float, boolean compile errors; ✅ requestsPerMonth silently wrong (but fix incomplete: changing var to long alone still overflows). ❌ "no package" ≠ launch error. ❌ cacheTtlMs called run time (overflow is silent). ❌ missed successRate = 0.0. ❌ / by zero called silently-wrong; proposed double fix → turns crash into silent Infinity. Didn't run the program. |
| 2026-09-19 | L3 Challenge (type choices) | ✅ 9/9 | No reasons given. |
| 2026-09-19 | L3 Quiz | 2/4 | Q1 ✅ Q4 ✅ (good business reasoning). Q2 ❌ confused with JS `var` hoisting. Q3 ⚠️ restated question, no reason. |

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
