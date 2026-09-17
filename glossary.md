# Glossary

Alphabetical. One or two lines each. Added as terms are introduced.

- **`ArrayIndexOutOfBoundsException`** — Runtime exception thrown when accessing an array index that doesn't exist. (JS would return `undefined`.)
- **Bytecode** — The instruction set of the JVM, stored in `.class` files. Produced by `javac`, platform-independent.
- **Bytecode verifier** — JVM step that checks loaded bytecode is well-formed and type-safe before executing it.
- **`ClassCastException`** — Runtime exception when casting an object to a type it isn't. Proof that Java checks types at runtime too.
- **Class file (`.class`)** — Compiled output of one class: bytecode + metadata (fields, methods, constant pool).
- **GC (Garbage Collector)** — The JVM component that automatically reclaims memory from unreachable objects.
- **GraalVM Native Image** — Tool that compiles Java ahead-of-time into a native executable: fast startup, low memory, reduced runtime dynamism.
- **JAR (Java ARchive)** — A zip file of `.class` files + resources + manifest. The unit of distribution (like a published npm package, but compiled).
- **`JAVA_HOME`** — Environment variable pointing to the JDK install directory; used by Maven, Gradle, IDEs.
- **JDK (Java Development Kit)** — Everything needed to develop Java: JVM + standard library + tools (`javac`, `jshell`, `jar`, `javap`…).
- **JIT (Just-In-Time) compiler** — Part of the JVM that compiles frequently executed ("hot") bytecode into native machine code at runtime.
- **`jlink`** — JDK tool that builds a minimal custom Java runtime containing only the modules your app needs.
- **JRE (Java Runtime Environment)** — JVM + standard library, enough to run programs. Oracle no longer ships one separately, but vendors (e.g. Temurin) still publish JRE builds.
- **JShell** — Java's REPL (since Java 9). Like typing `node` with no arguments.
- **JVM (Java Virtual Machine)** — The runtime that loads, verifies, and executes bytecode, and manages memory.
- **LTS (Long-Term Support)** — A Java release supported for years by vendors. Current: 25 (previous: 21, 17, 11, 8).
- **OpenJDK** — The open-source reference implementation of Java. Most distributions (Temurin, Corretto, Zulu, Oracle) are builds of it.
- **Source-file mode** — `java File.java`: compiles in memory and runs without writing `.class` files.
- **Standard library (JDK class library)** — Built-in packages like `java.lang`, `java.util`, `java.time`, `java.net.http`.
