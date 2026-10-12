package edu.aitu.facadebackup.subsystem;

import edu.aitu.facadebackup.domain.BackupEntry;
import edu.aitu.facadebackup.domain.VerificationReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Reads archive entries, checks them against the manifest, and reports every mismatch. */
public final class ArchiveInspector {
    private final ManifestCodec manifestCodec;

    public ArchiveInspector(ManifestCodec manifestCodec) {
        this.manifestCodec = manifestCodec;
    }

    public VerificationReport inspect(ZipFile zip) throws IOException {
        ZipEntry manifestFile = zip.getEntry(ManifestCodec.ENTRY_NAME);
        if (manifestFile == null) throw new IOException("Backup manifest is missing");
        List<BackupEntry> manifest = manifestCodec.decode(readAll(zip.getInputStream(manifestFile)));
        List<String> problems = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int checked = 0;
        for (BackupEntry expected : manifest) {
            if (!seen.add(expected.relativePath())) {
                problems.add("Duplicate manifest path: " + expected.relativePath());
                continue;
            }
            ZipEntry actual = zip.getEntry(expected.relativePath());
            if (actual == null || actual.isDirectory()) {
                problems.add("Missing file: " + expected.relativePath());
                continue;
            }
            checked++;
            ContentSummary content;
            try (InputStream input = zip.getInputStream(actual)) {
                content = summarize(input);
            }
            if (content.size() != expected.size()) problems.add("Size mismatch: " + expected.relativePath());
            if (!content.sha256().equals(expected.sha256())) problems.add("Checksum mismatch: " + expected.relativePath());
        }
        return new VerificationReport(problems.isEmpty(), checked, problems);
    }

    public List<BackupEntry> readManifest(ZipFile zip) throws IOException {
        ZipEntry manifestFile = zip.getEntry(ManifestCodec.ENTRY_NAME);
        if (manifestFile == null) throw new IOException("Backup manifest is missing");
        return manifestCodec.decode(readAll(zip.getInputStream(manifestFile)));
    }

    private static byte[] readAll(InputStream input) throws IOException {
        try (input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            input.transferTo(output);
            return output.toByteArray();
        }
    }

    private static ContentSummary summarize(InputStream input) throws IOException {
        java.security.MessageDigest digest;
        try {
            digest = java.security.MessageDigest.getInstance("SHA-256");
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new IllegalStateException("The Java runtime does not provide SHA-256", exception);
        }
        byte[] buffer = new byte[8192];
        long size = 0;
        int count;
        while ((count = input.read(buffer)) != -1) {
            digest.update(buffer, 0, count);
            size += count;
        }
        return new ContentSummary(size, java.util.HexFormat.of().formatHex(digest.digest()));
    }

    private record ContentSummary(long size, String sha256) { }
}
