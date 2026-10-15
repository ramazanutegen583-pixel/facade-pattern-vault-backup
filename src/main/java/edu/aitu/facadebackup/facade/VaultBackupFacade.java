package edu.aitu.facadebackup.facade;

import edu.aitu.facadebackup.domain.BackupEntry;
import edu.aitu.facadebackup.domain.VerificationReport;
import edu.aitu.facadebackup.subsystem.ArchiveAssembler;
import edu.aitu.facadebackup.subsystem.ArchiveInspector;
import edu.aitu.facadebackup.subsystem.BackupFileCollector;
import edu.aitu.facadebackup.subsystem.ChecksumService;
import edu.aitu.facadebackup.subsystem.ManifestCodec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

/** Small API for creating, checking, and restoring verified ZIP backups. */
public final class VaultBackupFacade {
    private final BackupFileCollector collector = new BackupFileCollector();
    private final ChecksumService checksumService = new ChecksumService();
    private final ManifestCodec manifestCodec = new ManifestCodec();
    private final ArchiveAssembler assembler = new ArchiveAssembler(manifestCodec);
    private final ArchiveInspector inspector = new ArchiveInspector(manifestCodec);
    private final Consumer<String> progress;

    public VaultBackupFacade() {
        this(message -> { });
    }

    /** Enables optional progress messages without exposing subsystem objects to the client. */
    public VaultBackupFacade(Consumer<String> progress) {
        this.progress = Objects.requireNonNull(progress, "progress");
    }

    public void createBackup(Path sourceDirectory, Path archive) throws IOException {
        progress.accept("[Facade] createBackup: coordinate file discovery, hashing, and ZIP creation.");
        Path source = sourceDirectory.toAbsolutePath().normalize();
        Path target = archive.toAbsolutePath().normalize();
        if (source.equals(target) || target.startsWith(source) && Files.isDirectory(target)) {
            throw new IOException("The archive path must not be the source directory");
        }
        progress.accept("  -> BackupFileCollector.collect: discover source files.");
        List<Path> files = collector.collect(source, target);
        progress.accept("     Found " + files.size() + " files.");
        List<BackupEntry> entries = new ArrayList<>();
        for (Path file : files) {
            String relative = source.relativize(file).toString().replace('\\', '/');
            if (ManifestCodec.ENTRY_NAME.equals(relative)) {
                throw new IOException("Source contains the reserved manifest path: " + relative);
            }
            progress.accept("  -> ChecksumService.sha256: " + relative);
            String hash = checksumService.sha256(file);
            entries.add(new BackupEntry(relative, Files.size(file), hash));
            progress.accept("     SHA-256: " + hash);
        }
        progress.accept("  -> ArchiveAssembler.assemble: write files and the ManifestCodec-encoded manifest.");
        assembler.assemble(source, target, files, entries);
        progress.accept("     Created ZIP: " + target);
    }

    public VerificationReport verifyBackup(Path archive) throws IOException {
        progress.accept("[Facade] verifyBackup: open the ZIP and delegate its inspection.");
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            progress.accept("  -> ArchiveInspector.inspect: read the manifest with ManifestCodec, compare sizes and SHA-256.");
            VerificationReport report = inspector.inspect(zip);
            progress.accept("     Valid: " + report.valid() + "; files checked: " + report.checkedFiles());
            return report;
        }
    }

    public void restoreBackup(Path archive, Path destination) throws IOException {
        progress.accept("[Facade] restoreBackup: validate the destination, verify the ZIP, then extract files.");
        Path target = destination.toAbsolutePath().normalize();
        if (Files.exists(target)) {
            try (var contents = Files.list(target)) {
                if (!Files.isDirectory(target) || contents.findAny().isPresent()) {
                    throw new IOException("Restore destination must be a new or empty directory: " + target);
                }
            }
        }
        Files.createDirectories(target);
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            progress.accept("  -> ArchiveInspector.inspect: verify the archive before extracting any files.");
            VerificationReport report = inspector.inspect(zip);
            if (!report.valid()) throw new IOException("Backup verification failed: " + String.join("; ", report.problems()));
            progress.accept("  -> ArchiveInspector.readManifest: obtain the verified file list using ManifestCodec.");
            for (BackupEntry entry : inspector.readManifest(zip)) {
                Path relative = Path.of(entry.relativePath()).normalize();
                if (relative.isAbsolute() || relative.getNameCount() == 0 || relative.startsWith("..")
                        || ManifestCodec.ENTRY_NAME.equals(entry.relativePath())) {
                    throw new IOException("Unsafe path in backup manifest: " + entry.relativePath());
                }
                Path output = target.resolve(relative).normalize();
                if (!output.startsWith(target)) throw new IOException("Unsafe path in backup manifest: " + entry.relativePath());
                Path parent = output.getParent();
                if (parent != null) Files.createDirectories(parent);
                var zipEntry = zip.getEntry(entry.relativePath());
                if (zipEntry == null) throw new IOException("Verified file disappeared: " + entry.relativePath());
                try (var input = zip.getInputStream(zipEntry)) {
                    Files.copy(input, output);
                }
                progress.accept("     Restored: " + entry.relativePath());
            }
        }
    }
}
