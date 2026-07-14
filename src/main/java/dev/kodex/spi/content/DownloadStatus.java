package dev.kodex.spi.content;

import java.nio.file.Path;
import java.util.List;

/**
 * Snapshot of a download's progress, reported by the core's download pipeline.
 *
 * @param state          current lifecycle state
 * @param progress       completion ratio in {@code [0.0, 1.0]}
 * @param message        optional human-readable status or error message
 * @param deliveredFiles files written into the staging dir so far (used for import on completion)
 */
public record DownloadStatus(
    State state,
    double progress,
    String message,
    List<Path> deliveredFiles
) {
    public DownloadStatus {
        deliveredFiles = deliveredFiles == null ? List.of() : List.copyOf(deliveredFiles);
    }

    public enum State {
        QUEUED,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public boolean isTerminal() {
        return state == State.COMPLETED || state == State.FAILED || state == State.CANCELLED;
    }

    public static DownloadStatus running(double progress, String message) {
        return new DownloadStatus(State.RUNNING, progress, message, List.of());
    }

    public static DownloadStatus completed(List<Path> deliveredFiles) {
        return new DownloadStatus(State.COMPLETED, 1.0, null, deliveredFiles);
    }

    public static DownloadStatus failed(String message) {
        return new DownloadStatus(State.FAILED, 0.0, message, List.of());
    }
}
