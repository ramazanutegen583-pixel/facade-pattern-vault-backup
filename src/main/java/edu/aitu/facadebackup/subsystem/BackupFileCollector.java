package edu.aitu.facadebackup.subsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Finds regular files below a source directory in stable relative-path order. */
public final class BackupFileCollector {
    public List<Path> collect(Path sourceDirectory, Path excludedFile) throws IOException {
        Path root = sourceDirectory.toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            throw new IOException("Source is not a directory: " + root);
        }
        Path excluded = excludedFile == null ? null : excludedFile.toAbsolutePath().normalize();
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(path -> Files.isRegularFile(path) && !Files.isSymbolicLink(path))
                    .filter(path -> excluded == null || !path.toAbsolutePath().normalize().equals(excluded))
                    .sorted(Comparator.comparing(path -> root.relativize(path).toString()))
                    .toList();
        }
    }
}
