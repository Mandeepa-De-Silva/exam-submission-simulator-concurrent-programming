# Exam Submission Simulator – Concurrent Programming

This Java project simulates a **high-concurrency online exam submission system** for thousands of students submitting answers simultaneously.  
It demonstrates real-world concurrent programming concepts with **thread safety**, **controlled concurrency**, **failure handling**, and **accurate performance metrics**.

### Key Goals of the System
- Process many student submissions concurrently
- Keep **exact counts** of successful/failed submissions
- Simulate **real-world failures** (network issues) with retry logic
- Produce **reliable statistics** (success rate, failure rate, timing) even under heavy load
- Avoid common concurrency problems: race conditions, thread explosion, deadlocks, inaccurate metrics

## Core Concurrency Design Decisions & Why

| Feature / Component              | Choice Made                              | Why this choice? (from notes)                                                                                          | Why NOT alternatives?                                                                                           |
|----------------------------------|------------------------------------------|------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| **Thread Management**            | `ExecutorService` with **fixed thread pool** (`newFixedThreadPool`) | Prevents **thread explosion** → no OutOfMemoryError under large number of tasks.<br>Controlled, predictable concurrency level.<br>Maximizes throughput without overwhelming system resources. | **CachedThreadPool** → unbounded thread creation → memory pressure, instability, high context-switching overhead under bursty/high load. |
| **Counter Updates**              | **`AtomicLong`** + CAS (Compare-And-Swap) operations | **Lock-free** → no blocking → better performance & scalability under high contention.<br>Guarantees **atomicity** and **visibility** without explicit locks.<br>No deadlock risk. | **synchronized** methods → blocking → reduces scalability under high load.<br>**ReentrantLock** → explicit locking → more complex code + risk of deadlock if not handled perfectly. |
| **Waiting for All Tasks**        | **`CountDownLatch`**                     | Main thread waits **safely** until **every** submission is processed.<br>Guarantees complete & accurate statistics.<br>No early/partial reporting. | Without latch → main thread may finish early → incomplete statistics, wrong success/failure rates.             |
| **Failure Handling**             | Custom **retry logic** (up to max attempts) | Simulates **real-world network unreliability**.<br>Increases overall system reliability.<br>Marks as failed only after max retries. | No retries → lower success rate even for transient failures → unrealistic simulation.                         |
| **Shared Resources Protection**  | Atomic variables for counters & stats    | Multiple threads access/modify shared counters concurrently → atomic operations prevent **race conditions**.<br>Avoids lost updates & unpredictable results. | Without protection → race conditions → incorrect counts, wrong success/failure rates & metrics.               |

### Why Atomic Variables Instead of synchronized / ReentrantLock?

- **AtomicLong** (chosen):
  - Lock-free using hardware-level **CAS** operations
  - No thread blocking → higher throughput under high load
  - Simpler code, no deadlock risk
  - Guarantees visibility & atomicity

- **synchronized** methods (considered but not used for counters):
  - Provides mutual exclusion but **blocks** threads
  - Reduces scalability when many threads contend for the lock
  - Still correct but worse performance under high load

- **ReentrantLock** (considered but not used):
  - Gives finer control & explicit locking
  - More complex code → higher risk of bugs/deadlocks
  - Overhead of manual lock/unlock → not needed for simple increments

→ **Conclusion**: Atomic variables give the **best balance** of correctness, performance, and simplicity for this use case.

### Why Fixed Thread Pool Instead of Cached / Unbounded?

- **Fixed pool** → limits active threads → controlled concurrency
- Prevents **resource exhaustion** even with thousands of submissions
- Predictable behavior under high load
- No thread creation overhead spikes

- **Cached pool** (not used) → creates new threads as needed → can lead to **thread explosion**
- Causes memory pressure, high context switching, instability

## What Could Go Wrong Without These Choices?

- Race conditions on counters → **lost updates** → wrong success/failure counts
- Inaccurate / unpredictable metrics & success rates
- Thread explosion → **OutOfMemoryError**
- Early statistics → incomplete results
- Deadlocks (if using locks incorrectly)
- System overload & crashes under high submission rates

## Technologies Used

- **Java** (core concurrency package: `java.util.concurrent`)
- `ExecutorService`, `AtomicLong`, `CountDownLatch`
- No external dependencies

## How to Run

1. Clone the repository
   ```bash
   git clone https://github.com/Mandeepa-De-Silva/exam-submission-simulator-concurrent-programming.git
