package edu.aitu.facadebackup.domain;

/** One file recorded in a backup manifest. */
public record BackupEntry(String relativePath, long size, String sha256) {
    public BackupEntry {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("relativePath must not be blank");
        }
        if (size < 0) {
            throw new IllegalArgumentException("size must not be negative");
        }
        if (sha256 == null || !sha256.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("sha256 must contain 64 lowercase hexadecimal characters");
        }
    }
}
