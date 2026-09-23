# Phase 0 — Environment & Toolchain — Notes

Concise revision notes for this phase only. Updated as topics are learned. See `notes.md` for the index of all phases.

---

## What Java is
- A **statically typed, compiled-to-bytecode, garbage-collected, class-based** language.
- "Java" means three things: the **language** (syntax/rules), the **platform** (JVM + standard library), and the **ecosystem** (Maven, Spring, etc.).
- Release cadence: a new version every 6 months (March/September); an **LTS** every 2 years. LTS line: 8, 11, 17, 21, **25**. Companies run LTS versions.

## JDK vs JRE vs JVM
- **JVM** — the virtual machine that loads `.class` bytecode and executes it (interprets + JIT-compiles hot code to native machine code). Also does memory management/GC.
- **JRE** — JVM + standard class library. Enough to *run* Java. Oracle stopped shipping a separate JRE after Java 8 (it was still bundled through 10), but vendors like Temurin still publish JRE builds/images (e.g. `eclipse-temurin:25-jre`) — useful for production containers.
- **JDK** — JRE + dev tools (`javac`, `jshell`, `jar`, `javap`, `jdb`, `jcmd`…). What developers install.
- Nesting: `JDK ⊃ JRE ⊃ JVM`.

## Compile → run pipeline
```
Hello.java  --javac-->  Hello.class (bytecode)  --java (JVM)-->  runs
```
- `javac Hello.java` → compile-time checks (types, syntax) → produces platform-independent bytecode.
- `java Hello` → starts a JVM, loads the class **by name** (not file name), calls `main`.
- `java Hello.java` → single-file source launch: compiles in memory and runs (handy for scripts/learning).
- "Write once, run anywhere": the *bytecode* is portable; the *JVM* is platform-specific.
- `javap -c Hello` shows the bytecode.
- A `.class` file is **self-contained**: the JVM never needs the `.java` source. That's why you deploy JARs, not source.
- `java Hello.java` compiles **in memory** and runs → no `.class` written to disk.

## Compile-time vs run-time errors
- **Compile time (`javac`)**: things provable from the code alone — wrong types (`int x = "a"`), syntax, public class name ≠ file name.
- **Run time (JVM)**: things that depend on actual values — e.g. `args[0]` with no args → `ArrayIndexOutOfBoundsException`. JS would silently give `undefined`; Java fails loudly at the exact line.

## Why bytecode (not native like Go/Rust)?
- ✅ Portability: one artifact for all OS/CPUs.
- ✅ JIT optimizes using **real runtime behaviour** (hot paths, actual types seen) — can beat static compilation for long-running servers.
- ✅ Classes can be **loaded dynamically at runtime** — Spring, Hibernate, and test frameworks depend on this.
- ✅ Bytecode **verifier** checks code is type-safe before running it.
- ❌ Slower startup + warm-up, more memory, needs a JVM present.
- Middle ground: **GraalVM Native Image** compiles ahead-of-time to a native binary (fast startup, less memory, loses some dynamism).

## Runtime type safety: Java vs TS
- TS `as User` is a compile-time promise only; nothing checks at runtime.
- Java casts are **checked by the JVM**: `(User) obj` on a non-User throws `ClassCastException` immediately.
- Objects always know their real class at runtime (`obj.getClass()`).

## Which Java is being used?
- `java` on the terminal comes from `PATH`. Maven/Gradle/IDEs use **`JAVA_HOME`** (or their own setting). They can differ!
- Diagnose: `java -version`, `echo $JAVA_HOME`, `mvn -v` (prints the Java it uses), `/usr/libexec/java_home -V` (lists installed JDKs).

## Production images
- Build stage: JDK (needs `javac`, Maven). Runtime stage: **JRE-only image** or a **`jlink`** custom runtime → smaller image, fewer tools for an attacker, less to patch.

## JShell (REPL)
- `jshell` to start, `/exit` to quit. Semicolons optional, no class needed.
- Bare expressions get auto-named vars: `7 / 2` → `$1 ==> 3`.
- Useful commands: `/vars`, `/methods`, `/imports`, `/list`, `/edit`, `/help`. Tab completion works (try `"abc".` + Tab).
- Use for: checking an API, quick experiments, predicting behaviour. Not for: real programs.

## Node comparison (mental bridge)
| Node | Java |
|---|---|
| `node` runtime (V8) | JVM |
| TS `tsc` → JS | `javac` → bytecode |
| Types erased, runtime is dynamic | Types checked at compile time AND the JVM verifies bytecode; runtime still knows classes |
| Single-threaded event loop | Real multi-threading (+ virtual threads) |
| npm | Maven / Gradle |

## Minimal program
```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello, JVM");
    }
}
```
- Every piece of code lives inside a class (no top-level functions in normal source files).
- A `public` class must live in a file with the **same name**: `Hello.java`.
- Since Java 25: **compact source files / instance main** — `void main() { IO.println("hi"); }` is valid in a single file. Good for scripts; real projects still use classes.

## Installing (macOS)
- Homebrew: `brew install openjdk@25` (or a distribution such as Temurin via `brew install --cask temurin@25`).
- Verify: `java -version`, `javac -version`.
- `JAVA_HOME` points tools (Maven, IDEs) at a specific JDK. On macOS: `/usr/libexec/java_home -v 25`.
