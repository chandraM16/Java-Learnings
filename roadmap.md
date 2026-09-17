# Roadmap — Java 0 → Production Spring Boot

Pacing is adaptive: fast through things familiar from TS/Node, slow on Java-specific ideas.
Each phase ends with a checkpoint assessment. Projects are placed where their prerequisites are met.

---

## Part A — Java the Language

### Phase 0 — Environment & Toolchain
0.1 What Java is; JDK vs JRE vs JVM; compile → bytecode → run
0.2 Installing JDK 25; `java`, `javac`, JShell, single-file source launch
0.3 IntelliJ IDEA setup; project structure; running & debugging
0.4 Maven in 20 minutes (just enough to run a project) *(deep dive in Phase 9)*

### Phase 1 — Fundamentals
1.1 Anatomy of a program: classes, `main`, statements, comments, naming conventions
1.2 Variables, primitive types, `var`, literals, casting, overflow
1.3 Reference types, `String`, immutability, `==` vs `equals`, `StringBuilder`
1.4 Operators & expressions (integer division, short-circuiting, precedence traps)
1.5 Control flow: `if`, classic & arrow `switch`, loops, `break`/`continue`, labels
1.6 Methods: parameters, return, overloading, scope, pass-by-value, `static`, `final`
1.7 Arrays: 1D/2D, `Arrays` utility, arrays vs lists preview
**Checkpoint 1**

### Phase 2 — Object-Oriented Java
2.1 Classes, objects, fields, constructors, `this`, object lifecycle
2.2 Encapsulation, access modifiers, invariants, immutability
2.3 `equals`, `hashCode`, `toString` — and why they break real apps
2.4 Static members, `final` classes/methods, packages & imports
2.5 Inheritance, overriding, `super`, polymorphism, dynamic dispatch
2.6 Abstract classes vs interfaces, default & static interface methods
2.7 Composition vs inheritance
2.8 Enums (with fields & behavior)
**Checkpoint 2** · **Project 1 kicks off**

### Phase 3 — Core Java: Collections & Generics
3.1 Collections framework map: `Collection`, `List`, `Set`, `Queue`, `Deque`, `Map`
3.2 `ArrayList` vs `LinkedList`, Big-O
3.3 `HashSet`/`HashMap` internals (hashing, buckets, why `hashCode` matters), `LinkedHashMap`
3.4 `TreeSet`/`TreeMap`, `Comparable`, `Comparator`
3.5 `ArrayDeque`, `PriorityQueue`, iteration & `ConcurrentModificationException`, immutable collections
3.6 Generics: classes, methods, bounded types, type erasure
3.7 Wildcards, `extends`/`super`, PECS
**Checkpoint 3**

### Phase 4 — Modern Java
4.1 Lambdas & functional interfaces (`Function`, `Predicate`, `Supplier`, `Consumer`)
4.2 Method references
4.3 Streams: pipelines, laziness, `map`/`filter`/`reduce`/`flatMap`
4.4 Collectors: `groupingBy`, `partitioningBy`, `toMap`, downstream collectors
4.5 `Optional` — correct and incorrect usage
4.6 Records
4.7 Sealed types, pattern matching (`instanceof`, `switch`, record patterns)
4.8 Switch expressions, text blocks, other modern features
**Checkpoint 4**

### Phase 5 — Exceptions
5.1 Exception hierarchy, checked vs unchecked
5.2 `try`/`catch`/`finally`, multi-catch, try-with-resources, `AutoCloseable`
5.3 Custom exceptions, wrapping & cause chains
5.4 Exception design: when to catch, when not to, vs Node error handling
**Checkpoint 5** · **Project 1 finishes**

### Phase 6 — I/O
6.1 `Path`, `Files` (NIO.2), reading/writing text & bytes
6.2 Streams, Readers/Writers, buffering, charsets
6.3 Classpath resources, serialization concepts
6.4 JSON with Jackson

### Phase 7 — Concurrency
7.1 Processes vs threads; Java's model vs Node's event loop
7.2 `Thread`, `Runnable`, `Callable`
7.3 `ExecutorService`, thread pools, `Future`
7.4 Race conditions, `synchronized`, visibility, Java Memory Model basics
7.5 Locks, atomics, concurrent collections
7.6 Deadlocks & thread-safety design
7.7 `CompletableFuture` (vs Promises)
7.8 Virtual threads & structured concurrency
**Checkpoint 7**

### Phase 8 — JVM Internals (introduced progressively; consolidated here)
8.1 JVM architecture, class loading, bytecode (`javap`)
8.2 Stack, heap, metaspace, allocation
8.3 Garbage collection concepts; G1, ZGC
8.4 JIT compilation
8.5 Profiling & diagnostics (JFR, JMC, heap dumps, thread dumps), tuning basics

### Phase 9 — Build Tools
9.1 Maven: `pom.xml`, coordinates, dependencies, scopes, transitive deps
9.2 Lifecycle, phases, plugins
9.3 Profiles, dependency management, BOMs, versions
9.4 Multi-module projects
9.5 Gradle reading knowledge (Kotlin DSL)

### Phase 10 — Testing
10.1 JUnit 5: structure, assertions, lifecycle, parameterized tests
10.2 AssertJ
10.3 Test doubles, Mockito, what (not) to mock
10.4 Integration tests, Testcontainers with PostgreSQL
**Project 2 (plain Java + PostgreSQL via JDBC)** — after Phases 6, 9, 10 basics

---

## Part B — Spring & Spring Boot

### Phase 11 — Spring Framework
11.1 Why Spring exists: build an app with manual wiring first, feel the pain
11.2 IoC & DI from first principles (write a toy container using reflection)
11.3 Annotations — what they are and who processes them; reflection
11.4 `ApplicationContext`, beans, scopes, lifecycle, component scanning
11.5 `@Configuration`/`@Bean`, profiles, properties, `Environment`
11.6 Proxies & AOP (the mechanism behind `@Transactional`, `@Async`, etc.)

### Phase 12 — Spring Boot
12.1 What Boot adds: starters, auto-configuration (and how to read the conditions report)
12.2 Embedded server, application lifecycle
12.3 `application.yml`, profiles, env vars, `@ConfigurationProperties`
12.4 Logging (SLF4J + Logback), Actuator basics

### Phase 13 — REST APIs with Spring MVC
13.1 DispatcherServlet request flow (vs Express)
13.2 Controllers, mappings, path vars, query params, bodies
13.3 DTOs, Jackson, validation (Jakarta Bean Validation)
13.4 `ResponseEntity`, status codes, `ProblemDetail`, global exception handling
13.5 Filters vs interceptors vs `@ControllerAdvice`
13.6 Pagination, sorting, filtering, API versioning
**Project 3 (Spring Boot REST API)**

### Phase 14 — Data
14.1 JDBC → `JdbcClient` / Spring JDBC
14.2 Flyway migrations
14.3 JPA & Hibernate: entities, persistence context, entity lifecycle
14.4 Relationships, cascades, orphan removal
14.5 Fetching: lazy vs eager, N+1, fetch joins, entity graphs
14.6 Spring Data repositories, JPQL, native queries, projections
14.7 Transactions: propagation, isolation, `@Transactional` pitfalls
14.8 Connection pools (HikariCP)

### Phase 15 — Spring Security
15.1 Security filter chain architecture
15.2 Authentication, `UserDetailsService`, password hashing
15.3 Sessions vs JWT; resource server
15.4 Authorization: roles, authorities, method security
15.5 OAuth2/OIDC concepts
15.6 CORS, CSRF, security best practices

### Phase 16 — Production Spring Boot
16.1 Structured logging, correlation IDs
16.2 Config & secrets management
16.3 Actuator: health, metrics (Micrometer), Prometheus/Grafana overview
16.4 OpenAPI / springdoc
16.5 Docker, multi-stage builds, Docker Compose (app + Postgres + Redis)
16.6 Caching with Redis
16.7 Messaging intro
**Project 4 (production-style app)**

---

## Part C — Advanced & Architecture

### Phase 17 — Advanced Backend Engineering
Async processing, event-driven design, Kafka, idempotency, retries & backoff, circuit breakers (Resilience4j), rate limiting, distributed tracing (OpenTelemetry), caching strategies, DB optimization, connection pools, performance tuning, outbox pattern.

### Phase 18 — Architecture
Layered, Clean, Hexagonal; DDD basics; modular monolith (Spring Modulith); microservices & boundaries; API contracts & compatibility; CQRS; dependency inversion; domain modeling. Always with trade-offs, never dogma.

### Phase 19 — Interview Preparation (woven in from Phase 3 onward, consolidated here)

---

## Projects

| # | Project | Domain | When | Key skills |
|---|---|---|---|---|
| 1 | **Ledger CLI** — personal expense tracker in the terminal | Personal finance | Phases 2–5 | OOP, collections, streams, records, enums, exceptions, file persistence (Phase 6 extension) |
| 2 | **Library Loans Service** — plain Java backend over PostgreSQL (no Spring) | Library lending | After Phases 6, 9, 10 | JDBC, transactions by hand, Maven, JUnit, Testcontainers — so you see what Spring later automates |
| 3 | **Event Booking REST API** | Events & ticketing | After Phase 13 | Spring Boot, MVC, DTOs, validation, error handling, pagination |
| 4 | **Clinic Appointments Platform** | Healthcare scheduling | After Phase 16 | JPA, Flyway, Spring Security (JWT + roles), tests, Docker, logging, OpenAPI, Actuator |
| 5 | **Order Fulfillment System** — modular monolith that can split into services | E-commerce | Phases 17–18 | Redis, Kafka, outbox, idempotency, async, resilience, observability, clean/hexagonal architecture, Testcontainers, perf |

Project domains may be adjusted based on the learner's interests.
