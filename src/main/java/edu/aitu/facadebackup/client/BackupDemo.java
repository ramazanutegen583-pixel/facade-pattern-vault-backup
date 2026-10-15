package edu.aitu.facadebackup.client;

import edu.aitu.facadebackup.domain.VerificationReport;
import edu.aitu.facadebackup.facade.VaultBackupFacade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Repeatable live demonstration; every business operation goes through the facade. */
public final class BackupDemo {
    private BackupDemo() { }

    /** Run this method directly in IntelliJ to demonstrate the complete workflow. */
    public static void main(String[] args) throws IOException {
        run();
    }

    static void run() throws IOException {
        Path runs = Path.of("target", "demo-runs").toAbsolutePath().normalize();
        Files.createDirectories(runs);
        Path session = Files.createTempDirectory(runs, "session-");
        Path source = session.resolve("source");
        Path archive = session.resolve("backup.zip");
        Path restored = session.resolve("restored");
        Map<Path, String> originals = createSampleFiles(source);
        VaultBackupFacade facade = new VaultBackupFacade(System.out::println);

        System.out.println("============================================================");
        System.out.println("VAULT BACKUP - LIVE FACADE PATTERN DEMONSTRATION");
        System.out.println("============================================================");
        System.out.println("Scenario: protect two documents and recover their original content after an accidental edit.");
        System.out.println("The client calls one facade; the facade coordinates five subsystem classes.");
        System.out.println("Demo folder: " + session);
        showFiles("ORIGINAL DOCUMENTS", source, originals.keySet());

        System.out.println("\n1. CLIENT CALL: facade.createBackup(source, archive)");
        facade.createBackup(source, archive);
        System.out.println("RESULT: real ZIP created (" + Files.size(archive) + " bytes).");

        System.out.println("\n2. CLIENT CALL: facade.verifyBackup(archive)");
        VerificationReport report = facade.verifyBackup(archive);
        if (!report.valid() || report.checkedFiles() != originals.size()) {
            throw new IOException("Demo verification failed: " + report.problems());
        }
        System.out.println("RESULT: both documents match the sizes and checksums in the manifest.");

        System.out.println("\n3. DEMO EVENT: accidentally overwrite the sample study notes after the backup.");
        Files.writeString(source.resolve("notes.txt"),
                "These study notes were accidentally overwritten after the backup.\n",
                StandardCharsets.UTF_8);
        showFiles("CURRENT SOURCE DOCUMENTS AFTER THE EDIT", source, originals.keySet());
        System.out.println("The ZIP still contains the original notes saved before this edit.");

        System.out.println("\n4. CLIENT CALL: facade.restoreBackup(archive, restored)");
        facade.restoreBackup(archive, restored);
        for (Map.Entry<Path, String> original : originals.entrySet()) {
            String recovered = Files.readString(restored.resolve(original.getKey()), StandardCharsets.UTF_8);
            if (!original.getValue().equals(recovered)) {
                throw new IOException("Restored file differs from its original content: " + original.getKey());
            }
            System.out.println("MATCH: " + original.getKey() + " - recovered content matches the original backup.");
        }
        showFiles("RESTORED DOCUMENTS - ORIGINAL NOTES RECOVERED", restored, originals.keySet());

        System.out.println("\nDEMONSTRATION COMPLETED SUCCESSFULLY");
        System.out.println("Facade benefit: the client requested three operations without calling any subsystem.");
        System.out.println("Inspect the actual results:");
        System.out.println("  Edited source documents: " + source);
        System.out.println("  ZIP snapshot:            " + archive);
        System.out.println("  Recovered documents:     " + restored);
        System.out.println("Run again to create a new demo folder automatically.");
    }

    private static Map<Path, String> createSampleFiles(Path source) throws IOException {
        Files.createDirectories(source.resolve("documents"));
        Map<Path, String> samples = new LinkedHashMap<>();
        samples.put(Path.of("notes.txt"),
                "My study notes\nThe Facade gives the client a simple API for a complex workflow.\n");
        samples.put(Path.of("documents", "project.txt"),
                "Course: Software Design Patterns\nProject: Vault Backup\nPattern: Facade\n");
        for (Map.Entry<Path, String> sample : samples.entrySet()) {
            Files.writeString(source.resolve(sample.getKey()), sample.getValue(), StandardCharsets.UTF_8);
        }
        return samples;
    }

    private static void showFiles(String title, Path root, Iterable<Path> relativePaths) throws IOException {
        System.out.println("\n" + title);
        for (Path relative : relativePaths) {
            System.out.println("  " + relative + ":");
            for (String line : Files.readAllLines(root.resolve(relative), StandardCharsets.UTF_8)) {
                System.out.println("    " + line);
            }
        }
    }
}
