package edu.aitu.facadebackup.subsystem;

import edu.aitu.facadebackup.domain.BackupEntry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Serializes the archive manifest as versioned UTF-8 tab-separated data. */
public final class ManifestCodec {
    public static final String ENTRY_NAME = "META-INF/backup-manifest.tsv";
    private static final String HEADER = "FACADE-BACKUP\t1";

    public byte[] encode(List<BackupEntry> entries) {
        StringBuilder text = new StringBuilder(HEADER).append('\n');
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        for (BackupEntry entry : entries) {
            String path = encoder.encodeToString(entry.relativePath().getBytes(StandardCharsets.UTF_8));
            text.append(path).append('\t').append(entry.size()).append('\t').append(entry.sha256()).append('\n');
        }
        return text.toString().getBytes(StandardCharsets.UTF_8);
    }

    public List<BackupEntry> decode(byte[] bytes) throws IOException {
        String[] lines = new String(bytes, StandardCharsets.UTF_8).split("\\R", -1);
        if (lines.length == 0 || !HEADER.equals(lines[0])) {
            throw new IOException("Unsupported or missing backup manifest header");
        }
        List<BackupEntry> entries = new ArrayList<>();
        Base64.Decoder decoder = Base64.getUrlDecoder();
        for (int index = 1; index < lines.length; index++) {
            String line = lines[index];
            if (line.isEmpty()) continue;
            String[] fields = line.split("\\t", -1);
            if (fields.length != 3) throw new IOException("Malformed manifest row " + (index + 1));
            try {
                String path = new String(decoder.decode(fields[0]), StandardCharsets.UTF_8);
                long size = Long.parseLong(fields[1]);
                entries.add(new BackupEntry(path, size, fields[2]));
            } catch (IllegalArgumentException exception) {
                throw new IOException("Invalid manifest row " + (index + 1), exception);
            }
        }
        return List.copyOf(entries);
    }
}
