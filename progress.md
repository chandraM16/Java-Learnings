# Learning Progress

_Last updated: 2026-09-24 (Session 9, cont'd)_

Status scale: Not Started → Introduced → Practiced → Comfortable → Mastered

## Current Phase
Phase 2 — Object-Oriented Java (Phase 0 items 0.3 IntelliJ / 0.4 Maven still deferred until just before Project 1)

## Current Topic
Lesson 2.3 (`equals`, `hashCode`, `toString`) — done, first pass, no revisions needed. Moving to 2.4 (static members, `final` classes/methods, packages & imports).

**Teaching-style correction (2026-09-24):** learner explicitly said lesson explanations were reading like revision of things already known, not first-time teaching — asked for plain language + code examples for every new term, not dense prose. Applied for the rest of this session (equals/hashCode/getClass/Objects.equals explanations); keep doing this by default: define term in plain words → tiny code example → then the real explanation. Don't revert to the denser Claude.md §6 format's prose style for genuinely new mechanisms.

**⚠️ Checkpoint 1 unresolved** (`assessments/checkpoint-1.md`) — provisional ~34/42, but blocked on Part C checklist items 2/3/7/9 (int[] for valid minutes, arrow switch, args re-parsed in 4 methods, pasted outputs) and Part D was only marginally at the 6/10 floor. On 2026-09-22 the learner explicitly chose to start Phase 2 now and revisit the checkpoint later rather than close it out first — this is a deliberate deferral of the normal "checkpoint gates next phase" rule, not a pass. Circle back to close it out; don't let it get lost.
L7 Ex3 closed unfinished (package + per-status counts + index-risk sentence) — same skills are tested in Checkpoint Part C.

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
| 1.4+1.5 Operators & control flow | Practiced | L5: Ex1 5/6, DiscountEngine 3/3 fixed, OrderStatusReport correct first try |
| 1.6 Methods | Practiced | L6: pass-by-value predictions 6/7, StatusTools 4/4 with correct categories, refactor into pure methods |
| 1.7 Arrays | Practiced | L7: predictions 8/9, BalanceStats 5/5 (incl. unprompted int-division fix) |

### Phase 2 — Object-Oriented Java
| Topic | Status | Evidence |
|---|---|---|
| 2.1 Classes, objects, fields, constructors, `this`, object lifecycle | Practiced | L2.1: Ex1 predicted the shadowing-causes-null bug correctly (exact string spacing off); Ex2 (Account, required) all 3 bugs correctly fixed, incl. unprompted use of `this(...)` chaining to sidestep the bug rather than patch around it; Ex3 (Product) fully correct after one review round (visibility private→public, price formatting `%.2f` applied from Phase 1 memory) |
| 2.2 Encapsulation, access modifiers, invariants, immutability | Practiced | L2.2: Ex1 (Wallet defensive-copy) — got the getter leak, missed the constructor leak (predicted 200, actual −50); Ex2 (MembershipCard, required) — validation logic (IllegalArgumentException vs IllegalStateException, correctly distinguished unprompted) right from v1; v1→v2 fixed private→public methods (2nd occurrence of the same visibility mistake from L2.1 Product), added missing getters, and replaced "throw as last line" with unprompted correct `try`/`catch` (Phase 5 material, used correctly incl. printing the exception). Left open by choice: constructor still accepts an unvalidated-strength starting `points` value (validates ≥0 but doesn't remove the parameter) |
| 2.3 `equals`, `hashCode`, `toString` | Practiced | L2.3: Ex1 (Coordinate, default Object behaviour) 4/4 correct first try. Ex2 (SeatAssignment, required) — correct `equals`/`hashCode`/`toString` overrides, `getClass()` guard used correctly, `final` fields (no mutable-hashCode trap), applied the Phase 1 `char + "" + int` concat trick unprompted inside `toString()`. Conceptual follow-up (equals-without-hashCode contract break) answered correctly. First lesson this phase with zero review round needed. |

### Phases 3–19
Not Started (see `roadmap.md`).

## Needs Review
- **Methods defaulting to `private` when they need to be callable from outside (2nd occurrence, 2026-09-24)** — `Product.restock`/`describe` (L2.1) then `MembershipCard.earnPoints`/`redeemPoints` (L2.2 v1). Both only compiled because `main` was in the same class. Fixed both times once flagged, but it's recurring — watch for a 3rd occurrence before assuming it's resolved.
- **Defensive copies — caught the getter leak, missed the constructor leak (L2.2 Ex1, 2026-09-24)** — predicted `Wallet` line 2 as `200`, actual `−50`: didn't extend "getter returns the live array" reasoning to "constructor never copied its parameter either," even though both are the same rule (`int[] b = a` shares the reference) applied at two different entry points to the object.
- **Validating a value vs. refusing to accept it at all (L2.2 Ex2, 2026-09-24)** — added `points < 0` validation to `MembershipCard`'s constructor instead of removing the `points` parameter as asked, closing the "negative" hole but not the "starting points shouldn't exist at all" invariant. Left open by learner's choice, not corrected — revisit if it recurs with a different invariant.
- **When does an unqualified name resolve to a field vs. a shadowing local/parameter (L2.1, 2026-09-23)** — in `Account`'s one-arg constructor, called `balance = 0.0;` "broken" when it actually correctly set the field (no parameter named `balance` in scope there); the real bug in that constructor was `owner` never being touched at all. Got the right final fix, but the write-up shows the *shadowing-only-when-a-name-collides* rule isn't fully solid yet. Written explanation for this wasn't collected (see Claude.md §10) — watch for it recurring in 2.2 (encapsulation/getters will exercise this same rule constantly).
- **Precise wording: reference copy vs object copy (L6)** — says "copied object". Java copies the reference (address); objects are never copied on a call. Recheck at Checkpoint 1.
- **Naming the error category** — 5 misses, then 4/4 correct in L6 Ex2 (2026-09-21) after the "did it throw?" test was given. Improving; confirm at Checkpoint 1.
- **Int division in ratios** — 3 misses (L3 ×2, L4), then applied correctly unprompted in L5 Ex3 (2026-09-21). Keep spot-checking; retest at Checkpoint 1.
- **No truthiness in Java (L3 recap)** — wrote `args[0] ? args[0] : "username"`. Conditions must be boolean expressions.
- **String concatenation rule (L4)** — expected `boolean + String` to be an error. Rule: if either operand of `+` is a String, the other is converted to text (works for every type, incl. null). Otherwise `+` is arithmetic — which is why `char + char` = int.
- **Applying integer-division rule in own code (L3)** — knows the rule in isolation (Ex1 ✅) but wrote `a.intValue() * 100 / b.intValue()` in Ex2. Knowledge → application gap.
- **Choosing types by meaning** — v1 used BigDecimal for counts (fixed in v2 ✅). 'Smallest unit = source of truth' now explained correctly in own words (2026-09-19) → verify in future code.
- **Following explicit instructions** — skipped requested comments/questions in consecutive submissions. Still happening (L6 Ex3: per-status counts missing twice, package, testability sentence). → Checkpoint 1 tasks will use an explicit requirements checklist to tick off.
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
| 2026-09-21 | L5 Quiz | Skipped | Learner moved to Lesson 6 |
| 2026-09-21 | L6 Ex1 Predictions | 6/7 | ✅ primitive copy (with reason), mutation via shared reference, String reassignment local, return-the-value, overload resolution incl. float→double & char→int. ❌ after `reset(label)` predicted "GOLD" — reset has no effect, so still "GOLD-VIP" (lost track that `tag` already mutated the shared object). Nit: wrote 10_000 (prints 10000). |
| 2026-09-21 | L6 Ex2 StatusTools | ✅ 4/4 | **All 4 categories correct — first time** (compile, run, 2× silently wrong). All fixes correct: return-the-value normalize, `(double)` cast + `double` return type propagated to `riskLevel`, final `return "LOW"`, guard clause. Minor: redundant ternary after guard; blank input → `Status: []`; unformatted 42.857142857142854%. Questions A + B (stack trace reading, why reassignment is local) not answered. |
| 2026-09-21 | L6 Ex2 Q A (stack trace) | ✅ | Crash = normalize line 3; bad value passed from main. (Extra nuance taught: null originated at the ternary, one step before the call.) |
| 2026-09-21 | L6 Ex2 Q B (why reassignment is local) | ⚠️ | Right conclusion (return + reassign). Imprecise: said "copied String object" — the *reference* is copied, never the object; and credited immutability for locality (reassignment is local for every type — cf. `reset(StringBuilder)`). |
| 2026-09-22 | L7 Ex3 countStatuses | 5/8 checklist | ✅ `private static int[] countStatuses`, named index constants, loop out of main, all helpers private, `formatSummary` now takes inputs only + uses `formatted` (applied L6 review), main ≤10 lines. ❌ per-status counts still not printed (3rd time); ❌ saved file has no `package` line and isn't under `com/chandan/orders/` (learner believed it was — possibly unsaved/misplaced); ❌ index-risk sentence missing. |
| 2026-09-22 | L7 Ex2 BalanceStats — `best` fix + Q B | ✅ | `Integer.MIN_VALUE` start; explained "finding a max needs the smallest possible start since values can be negative". Taught: MIN_VALUE vs `arr[0]` on empty arrays (silent vs loud). |
| 2026-09-22 | L7 Ex2 BalanceStats | 4/5 | ✅ `<` loop (run time), Arrays.toString (silent), copy-before-sort with correct "sharing the same reference" explanation (Q A ✅), **average `(double)` cast fixed unprompted** (but not listed in table). ❌ `best = 0` missed — asked "what is Best?" (domain meaning unclear, not a Java gap). Redundant alias `sortedArr = copyForSorting`. |
| 2026-09-22 | L7 Ex1 Predictions | 8/9 | ✅ identity vs Arrays.equals, aliasing via `c = a` (typo 90 for 99), doubleAll mutation, **`replace` has no effect → [6,2,4]** (fixes the L6 `reset` miss), null defaults, copyOf padding, uppercase-first sort. ❌ `grid[1].length` → said 3 (row 1 is `{4}` → 1). |
| 2026-09-21 | L6 Ex3 OrderStatusReport refactor | ✅ works / ⚠️ | Pure methods returning values ✅, `strip()` added (fixes " paid ") ✅, `total == 0` guard ✅. Missed again: per-status counts not printed (2nd time), no package, testability sentence. Design: `get` prefix on a calculation, `public` instead of `private`, `formatSummary` takes both `paid/total` and derived `paidPercentage` (can be inconsistent), counting loop still in main (can't return 5 counts from one method → motivates arrays/objects). |
| 2026-09-19 | L3 Quiz | 2/4 | Q1 ✅ Q4 ✅ (good business reasoning). Q2 ❌ confused with JS `var` hoisting. Q3 ⚠️ restated question, no reason. |
| 2026-09-23 | L2.1 Ex1 Parcel predictions | ✅ reasoning / ⚠️ exact output | Correctly predicted `trackingId` stays `null` in both objects (the shadowing bug — the point of the exercise). Off on exact string spacing (`null ( 2.5 kg)` vs actual `null (2.5kg)`) — reads reasoning right, literal output loosely. |
| 2026-09-23 | L2.1 Ex2 Account (REQUIRED) | ✅ | All 3 bugs found and fixed correctly on disk (`exercies/phase2/lesson1/src/com/chandan/bank/Account.java`), verified by compiling: `Meera: 600.0` / `Zoe: 0.0`. One-arg constructor fixed via `this(owner, 0.0)` chaining — better than a minimal per-field fix. Initial bug write-up mislabeled the one-arg constructor's `balance = 0.0;` as broken (it wasn't — no parameter shadows `balance` there, so it correctly sets the field; only `owner` was the real bug in that constructor). Category labels (compile/runtime/silent) and the two follow-up reasoning questions were not answered — learner asked to stop the per-exercise explain-in-own-words questions (see Claude.md §10, 2026-09-23 entry); agreed to gate them on novelty instead of dropping entirely. |
| 2026-09-23 | L2.1 Ex3 Product | ✅ after 1 review round | v1: correct constructor chaining, independent objects, unprompted correct money handling (`long` paise, cast-before-divide from Phase 1). Flagged: `restock`/`describe` `private` (only worked because `main` is in the same class) and unformatted price (`Rs 2000.0`). v2 fixed both (`public`, `"%.2f".formatted(...)`) — verified by compiling: `Rs 2000.00` / `Rs 4000.00`. |
| 2026-09-24 | L2.2 Ex1 Wallet predictions | 1/2 | ✅ line 1 (`999999`, getter leak). ❌ line 2 predicted `200`, actual `−50` — missed the constructor-level leak (`this.balances = balances;` never copies), only caught the getter-level one. |
| 2026-09-24 | L2.2 Ex2 MembershipCard (REQUIRED) v1 | 2/4 review points | ✅ validation logic + correct exception type per case (IllegalArgumentException vs IllegalStateException), unprompted and consistent. ❌ `earnPoints`/`redeemPoints` `private`. ❌ no `getMemberId()`/`getPoints()`. ❌ throwing call not last in `main` — dead `println` after it. ⚠️ constructor validates `points >= 0` but still accepts a starting value (spec: don't accept one at all). |
| 2026-09-24 | L2.2 Ex2 MembershipCard v2 | 3/4 fixed | ✅ methods → `public`. ✅ getters added. ✅ ordering fixed via unprompted correct `try`/`catch` (Phase 5 material) instead of just reordering — flagged `catch (Exception e)` as too broad for later. Verified output: `70` then the caught exception. ⚠️ constructor-accepts-starting-value point left open by choice, not fixed. |
| 2026-09-24 | L2.3 Ex1 Coordinate predictions | 4/4 | ✅ `==` false, `.equals()` false, `toString()` shape (`Coordinate@hash`), `hashCode()` mismatch — full correct reasoning about default `Object` behaviour with no overrides. |
| 2026-09-24 | L2.3 Ex2 SeatAssignment (REQUIRED) | ✅ first try | Verified by compiling: `false / true / A34 / 3010 / 3010`. Constructor validation, `equals`/`hashCode`/`toString` overrides, `getClass()` guard, `final` fields all correct. Conceptual question (equals-without-hashCode breaks the contract even without a crash) answered correctly, in own words, unprompted detail about *why* (wrong bucket lookup). |

## Assessments
| Date | Assessment | Result | Notes |
|---|---|---|---|
| 2026-09-22 | Checkpoint 1 (L1–L7) | Part A received (provisional 7.5/8 — #2 overflow ✅ but labelled "client error", expected "silently wrong"); Part B received (provisional 13.5/14 — all 7 bugs, all categories correct, output matches exactly; E6 'why' missing; rounds question unanswered); Part C v1 received (provisional 6.5/10 — output matches for all 4 commands, but checklist items 2 (int[] of valid minutes), 3 (arrow switch), 10 (paste outputs) missed; 'no valid' printed on one line; main 17 lines; constants not private/final; args re-parsed in 4 methods); Part D v1 received (provisional 6/10 — Q2 reference/copy ✅ precise, Q1 mechanism ✅ but no 'why logs are clean', Q3 no recommended form, Q4 categories without examples, Q5 unanswered). Running total ~34/42; **pass blocked on Part C items 2,3,7,9**. | Parts: A predict 8, B debug 14 (7 bugs: 1 compile/1 launch/1 run/4 silent), C build DeliveryStats 10, D explain 10 |

## Projects
- None yet.

## Strengths (evidence)
- Code quality improving fast: L5 Ex3 was correct on the first attempt, including the rule missed 3 times before.
- Writes working code quickly; applies fixes correctly once a bug is explained (AccessCheck 5/5).
- Started self-directed practice (`StringLearning.java`) without being asked — 2026-09-20.

## Confidence (mentor estimate)
- Java overall: 0.5/5
- Phase 0 concepts: 2/5
- Lesson 2 (packages/classpath): 3/5 — reasoning noticeably better than Lesson 1

## Next
1. Lesson 2.4 — Static members, `final` classes/methods, packages & imports (revisit `static` now that instance methods exist to contrast against).
2. Watch for a 3rd "method defaults to private" occurrence (see Needs Review) — if it recurs, it's a pattern worth naming explicitly rather than fixing silently each time.
3. Checkpoint 1 still unresolved (see Current Topic ⚠️) — blocked on Part C items 2,3,7,9 + Part D borderline. Deliberately deferred 2026-09-22; circle back before Checkpoint 2 at the latest.
4. Before Phase 2 / Project 1 wraps: install IntelliJ (0.3) + Maven quick start (0.4) — still not done.
