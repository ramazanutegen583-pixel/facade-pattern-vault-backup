# Home Theater Facade

Assignment 4 for Software Design Patterns, implemented with Java 17. This project uses the suggested Home Theater topic: one facade coordinates six independent devices to start, pause, resume, and end a movie session.

The equipment is simulated through Java object state and English console messages. No media files, external services, or physical devices are required.

## Run in IntelliJ IDEA

1. Open the project folder containing `pom.xml`, `src`, and `run-demo.cmd`.
2. If Windows created an extra outer folder when extracting the ZIP, select the inner project folder containing those files.
3. Open `pom.xml` as a Maven project, select **JDK 17 or later**, and wait for the import.
4. Open `src/main/java/edu/aitu/hometheater/client/HomeTheaterDemo.java`.
5. Click the green arrow next to `main()` and select **Run HomeTheaterDemo.main()**.

No program arguments are needed. The shared run configuration **Home Theater Demo** is also included.

## Run on Windows without IDE setup

Double-click `run-demo.cmd`. It finds JDK 17 or later, compiles the production sources, runs the complete demonstration, and keeps the console open. Maven is not required for this launcher.

The launcher checks `JAVA_HOME`, IntelliJ-managed JDKs in `%USERPROFILE%\.jdks`, `%ProgramFiles%\Java`, and `PATH`. A JRE without a compiler is insufficient. For an unattended run, use `run-demo.cmd --no-pause`.

## What the program shows

1. Starting **Interstellar**: popcorn is prepared, lights dim, the screen lowers, the projector and surround sound are configured, and playback begins.
2. Pausing: playback pauses and the room becomes brighter.
3. Resuming: movie lighting returns and playback continues.
4. Ending: playback stops, devices turn off, the screen rises, and full lighting returns.
5. Starting and ending **The Martian** using the same facade, demonstrating that another session works correctly.

Each client request is printed before the individual device actions. The final status is **Ready**. See the actual [console transcript](docs/demo-output.txt).

## Build and test

Requirements: JDK 17 or later and Maven 3.8 or later. The first Maven test run may download JUnit and Maven plugins. Production code uses only the Java standard library.

```shell
mvn clean verify
java -jar target/home-theater-facade-1.0.0.jar
```

Tests cover complete and repeated sessions, output order, pause/resume lighting, invalid titles and transitions, numeric boundaries, direct component use, and the absence of facade references in subsystem classes. See [validation results](docs/validation.md).

## Facade design

`HomeTheaterDemo` creates only `HomeTheaterFacade`. It never constructs or calls a subsystem. The facade exposes four operations plus a high-level status query:

```java
HomeTheaterFacade theater = new HomeTheaterFacade();
theater.watchMovie("Interstellar");
theater.pauseMovie();
theater.resumeMovie();
theater.endMovie();
```

The facade coordinates `Amplifier`, `StreamingPlayer`, `Projector`, `Screen`, `TheaterLights`, and `PopcornMaker`. Each component has its own focused behavior and remains publicly usable without the facade. No component holds a reference to the facade.

![UML class diagram](docs/class-diagram.png)

Invalid operations raise descriptive `IllegalArgumentException` or `IllegalStateException` messages. The facade validates movie titles and session state before changing devices. The demo follows the valid workflow, so it finishes without an error stack trace.

## Project structure

```text
src/main/java/edu/aitu/hometheater/
  client/       HomeTheaterDemo
  facade/       HomeTheaterFacade
  subsystem/    six focused device classes
src/test/java/  JUnit tests
.run/           shared IntelliJ configuration
docs/           UML diagram, console transcript, validation results
reports/        English assignment report
```

The [assignment report](reports/home-theater-facade-report.docx) explains the chosen subsystem, UML relationships, five Clean Code principles with code excerpts, validation, and the design conclusion.

Repository: https://github.com/ramazanutegen583-pixel/facade-pattern-vault-backup
