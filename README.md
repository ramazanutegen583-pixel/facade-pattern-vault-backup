# Vault Backup Facade

An educational Java 17 implementation of the Facade pattern. `VaultBackupFacade` turns a multi-step backup workflow into three client operations: create, verify, and restore.

## Why a facade

A backup client would otherwise need to discover files, calculate SHA-256 checksums, build a versioned manifest, assemble a ZIP archive, open that archive, and compare each stored file with its manifest entry. The facade coordinates these steps and applies the correct order. The subsystem classes remain independent and can still be used by advanced clients.

## Requirements

- JDK 17 or later
- Maven 3.8 or later

## Build

```shell
mvn clean package
```

## Run

### Windows launcher

Extract the ZIP download, then double-click `run-demo.cmd` in the project folder. The launcher locates JDK 17 or later, compiles the Java sources, runs the complete demonstration, and keeps the console open. It does not require Maven or an IntelliJ project import. A JDK is required; the launcher checks `JAVA_HOME`, IntelliJ-managed JDKs under `%USERPROFILE%\.jdks`, `%ProgramFiles%\Java`, and `PATH`.

### Open the project in IntelliJ

Open the folder containing `pom.xml`, `src`, and `run-demo.cmd`. If Windows created an extra outer extraction folder, open the inner project folder. Open `pom.xml` as a Maven project, select JDK 17 or later, and wait for the import to finish before running `BackupDemo.main()`.

### Live demonstration for the defense

In IntelliJ IDEA, open `client/BackupDemo.java` and run **BackupDemo.main()** with the green gutter arrow. No program arguments are needed. You can also run `BackupCli.main()` with no arguments or set its **Program arguments** to `demo`. The working directory should be the project folder containing `pom.xml`.

The repository also includes a shared IntelliJ run configuration named **Facade Demo**.

```shell
java -jar target/facade-backup-1.0.0.jar demo
```

The demo creates two sample documents, prints their contents, calls the facade to create a real ZIP, and verifies its checksums. It then simulates an accidental edit to the sample notes and restores the original content from the backup. The demo compares the recovered content with the original text and prints the result. Console progress identifies the facade calls and the subsystem responsibilities as they execute. Results remain under `target/demo-runs/session-*` so you can compare the edited source documents with the ZIP snapshot and the recovered documents. Every run creates a new session directory, so repeated demonstrations do not need manual cleanup.

`BackupDemo` is a demonstration client: its backup, verification, and restore operations call only `VaultBackupFacade`. The optional progress callback prints the facade's actual delegation steps; normal CLI commands keep their existing concise output.

### Individual operations

```shell
java -jar target/facade-backup-1.0.0.jar backup ./sample-data ./sample-backup.zip
java -jar target/facade-backup-1.0.0.jar verify ./sample-backup.zip
java -jar target/facade-backup-1.0.0.jar restore ./sample-backup.zip ./restored-data
```

Restore requires a new or empty destination directory. Verification reports missing files, size mismatches, and checksum mismatches. The archive contains the original relative paths and a UTF-8 TSV manifest at `META-INF/backup-manifest.tsv`.

## Design

![UML class diagram](docs/class-diagram.png)

The CLI client calls only `VaultBackupFacade`. The facade coordinates `BackupFileCollector`, `ChecksumService`, `ManifestCodec`, `ArchiveAssembler`, and `ArchiveInspector`. The subsystem classes do not refer back to the facade.

See [the assignment report](reports/facade-pattern-report.docx) for the introduction, UML explanation, five Clean Code principles with before-and-after excerpts, and conclusion.

## Project structure

```text
src/main/java/edu/aitu/facadebackup/
  client/       BackupCli and the live BackupDemo
  domain/       BackupEntry, VerificationReport
  facade/       VaultBackupFacade
  subsystem/    file collection, checksums, manifest, archive assembly and inspection
docs/           UML class diagram
reports/        English assignment report
```
