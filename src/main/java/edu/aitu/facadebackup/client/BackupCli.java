package edu.aitu.facadebackup.client;

import edu.aitu.facadebackup.domain.VerificationReport;
import edu.aitu.facadebackup.facade.VaultBackupFacade;

import java.nio.file.Path;

/** Command-line client; it depends only on the facade's public API. */
public final class BackupCli {
    private BackupCli() { }

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || (args.length == 1 && "demo".equals(args[0]))) {
            BackupDemo.run();
            return;
        }
        if (args.length < 2 || args.length > 3) {
            printUsage();
            System.exit(2);
        }
        VaultBackupFacade backup = new VaultBackupFacade();
        switch (args[0]) {
            case "backup" -> {
                requireArgumentCount(args, 3);
                backup.createBackup(Path.of(args[1]), Path.of(args[2]));
                System.out.println("Backup created: " + args[2]);
            }
            case "verify" -> {
                requireArgumentCount(args, 2);
                VerificationReport report = backup.verifyBackup(Path.of(args[1]));
                System.out.printf("Valid: %s; files checked: %d%n", report.valid(), report.checkedFiles());
                report.problems().forEach(problem -> System.out.println("- " + problem));
                if (!report.valid()) System.exit(1);
            }
            case "restore" -> {
                requireArgumentCount(args, 3);
                backup.restoreBackup(Path.of(args[1]), Path.of(args[2]));
                System.out.println("Backup restored to: " + args[2]);
            }
            default -> {
                System.err.println("Unknown command: " + args[0]);
                System.exit(2);
            }
        }
    }

    private static void requireArgumentCount(String[] args, int expected) {
        if (args.length != expected) {
            System.err.println("Wrong number of arguments for '" + args[0] + "'.");
            printUsage();
            System.exit(2);
        }
    }

    private static void printUsage() {
        System.err.println("Usage: demo | backup <source-dir> <archive.zip> | verify <archive.zip> | restore <archive.zip> <destination-dir>");
        System.err.println("Run with no arguments to start the live Facade demonstration.");
    }
}
