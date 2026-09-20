# Learning Progress

_Last updated: 2026-09-20 (Session 5, cont.)_

Status scale: Not Started → Introduced → Practiced → Comfortable → Mastered

## Current Phase
Phase 1 — Java Fundamentals (Phase 0 items 0.3 IntelliJ / 0.4 Maven deferred until just before Project 1)

## Current Topic
Lesson 5 — Operators & control flow. Ex1 5/6, Ex2 ✅, Ex3 ✅ (2026-09-21) — **first exercise with the int-division cast applied unprompted.** Awaiting L5 quiz → then Lesson 6.
Lesson 4 closed by learner request; EmailMasker v2 + L4 quiz not submitted. Open L4 items (substring off-by-one, String vs StringBuilder, `==` vs equals wording) folded into L5 Ex2/Ex3.

Phase 1 remaining after L5: L6 Methods, L7 Arrays, Checkpoint 1.

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
| 1.3 Strings & reference types | Practiced | L4: `==`/equals understood + applied in AccessCheck; EmailMasker worked but had off-by-one + StringBuilder misuse |
| 1.4+1.5 Operators & control flow | Introduced | Lesson 5 delivered |
| 1.6–1.7 | Not Started | |

### Phases 2–19
Not Started (see `roadmap.md`).

## Needs Review
- **Naming the error category (5th miss)** — fixes bugs correctly but calls silent wrong-output bugs "run time". Anchor: *did anything throw / stop the program?* No → silently wrong. Test again at Checkpoint 1.
- **Int division in ratios** — 3 misses (L3 ×2, L4), then applied correctly unprompted in L5 Ex3 (2026-09-21). Keep spot-checking; retest at Checkpoint 1.
- **No truthiness in Java (L3 recap)** — wrote `args[0] ? args[0] : "username"`. Conditions must be boolean expressions.
- **String concatenation rule (L4)** — expected `boolean + String` to be an error. Rule: if either operand of `+` is a String, the other is converted to text (works for every type, incl. null). Otherwise `+` is arithmetic — which is why `char + char` = int.
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
| 2026-09-19 | L3 MetricsReport run + Q#5 re-explain | Skipped | Learner moved to Lesson 4 |
| 2026-09-20 | L4 Ex1 Predictions | 6/8 | ✅ ==/equals with correct reasons, substring, indexOf, parseInt, `1+2+"3"+4+5`. ❌ merged the two charAt lines → missed 171 and "Ja". ❌ thought `boolean + String` is an error (it's "true false"). |
| 2026-09-20 | L4 Ex2 AccessCheck — ran 4 ways + bug table | 3.5/5 | ✅ Ran all 4 (first time following the run-it instruction). ✅ `==` bug + fix `"admin".equals(role)`. ✅ overflow + `1000L` fix. ✅ args[0] run time. ⚠️ #1: caught the NPE but missed that `role.toLowerCase();` discards its result (immutability). ❌ #5 int division (`2/7*100` → 0.0) missed even after a hint — 3rd exposure. ⚠️ Proposed JS-style ternary `args[0] ? ... : ...` (no truthiness in Java). |
| 2026-09-20 | L4 Ex2 AccessCheck — fixed | ✅ 5/5 | `equalsIgnoreCase`, `(double)` cast, `1000L`, null/length guards. Deviation: silently defaults username to "guest" instead of printing the required message. |
| 2026-09-20 | L4 Ex3 EmailMasker | ⚠️ works, design issues | ✅ strip/toLowerCase, indexOf guard, `repeat()`, handled no-`@` case. ❌ `substring(idx)` includes the `@` (off-by-one). ❌ used StringBuilder for read-only text + mutating `replace(...)`. ❌ blank input not handled (prints empty Normalized). ❌ leftover debug print, missing "Masked" label. |
| 2026-09-20 | Self-study `StringLearning.java` | 👍 | Learner experimented with lesson features unprompted (StringBuilder aliasing, split, text block, parseInt). Note: wrapped everything in `try/catch(Exception)`; comments for parseInt/valueOf are swapped. |
| 2026-09-20 | L4 Ex3 v2 + L4 Quiz | Skipped | Learner moved to Lesson 5 |
| 2026-09-20 | L5 Ex1 Predictions | 5/6 | ✅ switch expression, multi-label, `if (flag = true)` → A/true, `continue` → 12, short-circuit null guard. ❌ `byte b += 300` → said 310 (actual 54: += casts, value wraps to 8 bits). |
| 2026-09-20 | L5 Ex2 DiscountEngine | ✅ code / ❌ categories | Found & fixed all 3 bugs correctly; converted to arrow switch; kept `%.2f` formatting from L3. ❌ Called fall-through and `if (flag = true)` "run time" — all 3 are silently wrong (asked explicitly, 5th category miss). Didn't answer Q3 (why gold gets less). Used arrow switch as statements, not as an expression. `isPremium == true` instead of `isPremium`. |
| 2026-09-21 | L5 Ex3 OrderStatusReport | ✅ | Output matches spec exactly. Guard clause + `return`, enhanced for, arrow switch, `toLowerCase()` normalisation, **`(double)` cast applied without being prompted** (first time), `%.2f`. Gaps: per-status counts computed but never printed (spec item); no `strip()` so `" paid "` counts as unknown; 5 parallel counters (→ Map/enum later); long `+` chain instead of `formatted`. |
| — | L5 Quiz | Pending | |
| 2026-09-19 | L3 Quiz | 2/4 | Q1 ✅ Q4 ✅ (good business reasoning). Q2 ❌ confused with JS `var` hoisting. Q3 ⚠️ restated question, no reason. |

## Assessments
- None yet.

## Projects
- None yet.

## Strengths (evidence)
- Code quality improving fast: L5 Ex3 was correct on the first attempt, including the rule he had missed 3 times before.
- Writes working code quickly; applies fixes correctly once a bug is explained (AccessCheck 5/5).
- Started self-directed practice (`StringLearning.java`) without being asked — 2026-09-20.

## Confidence (mentor estimate)
- Java overall: 0.5/5
- Phase 0 concepts: 2/5
- Lesson 2 (packages/classpath): 3/5 — reasoning noticeably better than Lesson 1

## Next
1. Review Lesson 4 (key signals: did they RUN AccessCheck? do they catch `==` on strings, ignored `toUpperCase()` result, NPE, plus the L3 int-division/overflow bugs?).
2. Lesson 5: Operators (Java-specific only) + control flow (`if`, arrow `switch`, loops).
3. Lesson 6: Methods. Lesson 7: Arrays. Then Checkpoint 1.
