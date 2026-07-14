package dev.kodex.spi.common.http;

/**
 * Thrown by a provider when the upstream service rate-limits it (HTTP 429, or an equivalent quota
 * error). The core reacts by putting the provider on a cooldown: it is skipped for the next
 * {@code retryAfterSeconds} (or a server-configured default when the upstream give no hint) instead
 * of hammering the service for every series/book in the refresh queue.
 *
 * <p>Providers should throw this from their HTTP helper instead of swallowing the 429 — any other
 * exception is treated as a per-item failure, and the provider is retried on the very next item.
 */
public class ProviderRateLimitException extends RuntimeException {

    /** Cooldown hint from the upstream {@code Retry-After} header, or null when it gave none. */
    private final Long retryAfterSeconds;

    public ProviderRateLimitException(String message) {
        this(message, null);
    }

    public ProviderRateLimitException(String message, Long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public Long retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
