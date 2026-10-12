package edu.aitu.facadebackup.subsystem;

import edu.aitu.facadebackup.domain.BackupEntry;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Writes source files and their manifest to a ZIP archive. */
public final class ArchiveAssembler {
    private final ManifestCodec manifestCodec;

    public ArchiveAssembler(ManifestCodec manifestCodec) {
        this.manifestCodec = manifestCodec;
    }

    public void assemble(Path sourceRoot, Path archive, List<Path> files, List<BackupEntry> entries) throws IOException {
        Path root = sourceRoot.toAbsolutePath().normalize();
        Path target = archive.toAbsolutePath().normalize();
        Path parent = target.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (OutputStream fileOutput = Files.newOutputStream(target);
             ZipOutputStream zip = new ZipOutputStream(fileOutput)) {
            byte[] buffer = new byte[8192];
            for (int index = 0; index < files.size(); index++) {
                Path file = files.get(index);
                String name = root.relativize(file.toAbsolutePath().normalize()).toString().replace('\\', '/');
                zip.putNextEntry(new ZipEntry(name));
                try (var input = Files.newInputStream(file)) {
                    int count;
                    while ((count = input.read(buffer)) != -1) zip.write(buffer, 0, count);
                }
                zip.closeEntry();
            }
            zip.putNextEntry(new ZipEntry(ManifestCodec.ENTRY_NAME));
            zip.write(manifestCodec.encode(entries));
            zip.closeEntry();
        }
    }
}
