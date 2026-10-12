package edu.aitu.facadebackup.domain;

import java.util.List;

/** Outcome of checking all files recorded in a backup archive. */
public record VerificationReport(boolean valid, int checkedFiles, List<String> problems) {
    public VerificationReport {
        problems = List.copyOf(problems);
    }
}
