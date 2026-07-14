package dev.kodex.spi.common.http;

import dev.kodex.spi.KodexExtension;
import okhttp3.OkHttpClient;

/**
 * Supplies the core-configured {@link OkHttpClient} that all plugin outbound HTTP should go through.
 *
 * <p>The host owns the network policy — proxy (HTTP / SOCKS4 / SOCKS5) and DNS-over-HTTPS — and applies
 * it to this client. Plugins must call {@link #httpClient()} <em>per request</em> rather than caching the
 * instance, because the host rebuilds it when the operator changes the network settings.
 *
 * <p>A plugin receives the provider through {@link KodexExtension#setHttpClientProvider}.
 * Build per-request tweaks (timeouts, interceptors) with {@code httpClient().newBuilder()} so the shared
 * connection pool, proxy and resolver are preserved.
 */
public interface HttpClientProvider {

    /** The current shared client, reflecting the latest proxy / DoH settings. Never {@code null}. */
    OkHttpClient httpClient();

    /**
     * Like {@link #httpClient()} but transparently solves Cloudflare anti-bot challenges: when a response
     * is a Cloudflare challenge (HTTP 403/503), the host re-runs the request through the operator-configured
     * solver (FlareSolverr / Byparr), then replays it with the issued {@code cf_clearance} cookie and the
     * solver's User-Agent (reused for later requests to the same host so the clearance keeps validating).
     *
     * <p>A source that scrapes a site sitting behind Cloudflare should fetch through this client instead of
     * {@link #httpClient()}. It shares the same connection pool, proxy and resolver — it only adds the
     * challenge handling and a clearance-cookie store. When no solver is configured, it behaves exactly like
     * {@link #httpClient()} (challenges pass through unsolved), so it is always safe to use. Never {@code null}.
     */
    default OkHttpClient cloudflareClient() {
        return httpClient();
    }
}
