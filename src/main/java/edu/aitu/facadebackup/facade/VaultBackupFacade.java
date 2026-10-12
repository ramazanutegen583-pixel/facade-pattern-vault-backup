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
import java.util.zip.ZipFile;

/** Small API for creating, checking, and restoring verified ZIP backups. */
public final class VaultBackupFacade {
    private final BackupFileCollector collector = new BackupFileCollector();
    private final ChecksumService checksumService = new ChecksumService();
    private final ManifestCodec manifestCodec = new ManifestCodec();
    private final ArchiveAssembler assembler = new ArchiveAssembler(manifestCodec);
    private final ArchiveInspector inspector = new ArchiveInspector(manifestCodec);

    public void createBackup(Path sourceDirectory, Path archive) throws IOException {
        Path source = sourceDirectory.toAbsolutePath().normalize();
        Path target = archive.toAbsolutePath().normalize();
        if (source.equals(target) || target.startsWith(source) && Files.isDirectory(target)) {
            throw new IOException("The archive path must not be the source directory");
        }
        List<Path> files = collector.collect(source, target);
        List<BackupEntry> entries = new ArrayList<>();
        for (Path file : files) {
            String relative = source.relativize(file).toString().replace('\\', '/');
            if (ManifestCodec.ENTRY_NAME.equals(relative)) {
                throw new IOException("Source contains the reserved manifest path: " + relative);
            }
            entries.add(new BackupEntry(relative, Files.size(file), checksumService.sha256(file)));
        }
        assembler.assemble(source, target, files, entries);
    }

    public VerificationReport verifyBackup(Path archive) throws IOException {
        try (ZipFile zip = new ZipFile(archive.toFile())) {
            return inspector.inspect(zip);
        }
    }

    public void restoreBackup(Path archive, Path destination) throws IOException {
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
            VerificationReport report = inspector.inspect(zip);
            if (!report.valid()) throw new IOException("Backup verification failed: " + String.join("; ", report.problems()));
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
            }
        }
    }
}
