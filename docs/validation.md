# Validation results

Performed on 18 October 2026 on Windows with Microsoft JDK 17.0.20.1.

## Automated tests

Command: `mvn -B clean verify`

Result: **34 tests, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS.**

The JUnit suite checks:

- Complete sessions, pause/resume, shutdown while paused, and repeated sessions.
- Ready, Playing, and Paused status transitions.
- Startup and shutdown message order and brightness during pause/resume.
- Rejection of null, blank, and whitespace-only titles before device changes.
- Invalid and duplicate session operations without device side effects.
- Volume and brightness boundaries and independent device behavior.
- Power prerequisites and state reset after shutdown.
- The complete demonstration output.
- Client source dependencies and the absence of facade references in subsystem sources.

## Packaged application

Command: `java -jar target/home-theater-facade-1.0.0.jar`

The packaged application was launched twice. Both runs produced identical 75-line English output and exited successfully. The actual program output is saved in [demo-output.txt](demo-output.txt).

## Windows launcher

Command: `run-demo.cmd --no-pause`

The launcher found the installed JDK, compiled the production sources, and completed both movie sessions successfully without Maven. The default double-click mode pauses at the end so the output remains visible.

## IntelliJ IDEA on the user's computer

A clean Git clone was created in `C:\Users\ramaz\Documents\Codex\HomeTheaterFacade`. Its `pom.xml` was opened as a project in the installed **IntelliJ IDEA 2025.3.3**. Maven import recognized the Java module and the main method's green Run control.

`HomeTheaterDemo.java` was run directly from IntelliJ with no program arguments. The console showed:

```text
FINAL STATUS: Ready
DEMONSTRATION COMPLETED SUCCESSFULLY
Process finished with exit code 0
```

The project includes the shared **Home Theater Demo** application configuration. Open the folder containing `pom.xml`, rather than an extra ZIP extraction folder. An installed JDK 17 or later is required.

## Report and assignment requirements

The four-page English DOCX report was rendered and all pages were visually inspected. The UML diagram includes the client, facade, six independent subsystems, and the direction of their relationships. An editable PlantUML source is also included.

| Assignment requirement | Implementation |
| --- | --- |
| Java 17 | Maven release 17 and JDK 17 compilation |
| Suitable topic | Suggested Home Theater scenario |
| At least three subsystems | Six focused public device classes |
| Simple facade | Four movie operations and a status query |
| Client uses only the facade | HomeTheaterDemo constructs only HomeTheaterFacade |
| Subsystems unaware of facade | Device classes have no facade references |
| Direct subsystem access | Public devices are also exercised independently by tests |
| At least five Clean Code principles | Five annotated implementation excerpts in the report |
| UML, introduction, conclusion, repository link | Included in the rewritten English report |
| Runnable demonstration | No-argument main, shared IDE configuration, executable JAR, Windows launcher |

Device operations simulate equipment by changing Java object state and printing messages. They do not play video or communicate with physical hardware.
