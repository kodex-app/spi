package dev.kodex.spi.common.http;

/**
 * Thrown by a provider when it could not get an answer out of its upstream site: the request failed
 * at the transport (DNS, TLS, timeout, connection reset), came back with a non-2xx status, or
 * returned a body the provider could not parse at all.
 *
 * <p>Providers used to answer such a failure with an <em>empty</em> result — an empty
 * {@link dev.kodex.spi.content.SeriesPage}, an empty chapter list, a null document swallowed into
 * {@code SeriesPage.empty()}. That is indistinguishable from a site which genuinely has nothing, so
 * a source that was blocked, moved, or redesigned showed up in the apps as "nothing to show here"
 * and there was no way to tell the two apart without reading the server log. Throwing this instead
 * lets the core answer <b>502 Bad Gateway</b> with the real reason, which the clients display.
 *
 * <p>Use {@link ProviderRateLimitException} for an HTTP 429 — that one is a cooldown the core
 * schedules around (a download waits it out and retries), not a failure to report.
 *
 * <p>Background work is unaffected: a library refresh already logs and skips a series whose source
 * call fails, and a download job already fails that one job, so nothing has to catch this.
 */
public class SourceUnavailableException extends RuntimeException {

    /** Upstream HTTP status, or null when the request never completed (transport failure, bad body). */
    private final Integer statusCode;

    /** The URL that was being fetched, or null when the failure isn't tied to one request. */
    private final String url;

    public SourceUnavailableException(String message) {
        this(message, null, null, null);
    }

    public SourceUnavailableException(String message, Throwable cause) {
        this(message, null, null, cause);
    }

    public SourceUnavailableException(String message, Integer statusCode, String url, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.url = url;
    }

    /**
     * The upstream answered, but not with success. {@code body} is the response body (or any part of
     * it) — it is trimmed to a short snippet, because a Cloudflare challenge page or an API error
     * object usually says in its first line exactly what the source needs to be told.
     *
     * @param source display name of the source, so a multi-source failure names the culprit
     */
    public static SourceUnavailableException http(String source, String url, int status, String body) {
        String snippet = snippet(body);
        return new SourceUnavailableException(
            source + ": HTTP " + status + " from " + url + (snippet.isEmpty() ? "" : " — " + snippet),
            status, url, null);
    }

    /** The request never completed: DNS, TLS, timeout, connection reset, the site being gone. */
    public static SourceUnavailableException transport(String source, String url, Throwable cause) {
        return new SourceUnavailableException(
            source + ": couldn't reach " + url + " (" + describe(cause) + ")", null, url, cause);
    }

    /**
     * The request succeeded but the body was unusable — not the JSON/HTML the source expects. Almost
     * always an interstitial (challenge, consent wall, "site moved") or an upstream redesign, so it is
     * reported like any other outage rather than parsed around.
     */
    public static SourceUnavailableException unreadable(String source, String url, String detail) {
        return new SourceUnavailableException(
            source + ": unreadable response from " + url + " (" + detail + ")", null, url, null);
    }

    /** Upstream HTTP status, or null when the request never completed. */
    public Integer statusCode() {
        return statusCode;
    }

    /** The URL being fetched, or null. */
    public String url() {
        return url;
    }

    private static String snippet(String body) {
        if (body == null) {
            return "";
        }
        String flat = body.replaceAll("\\s+", " ").trim();
        return flat.length() <= 200 ? flat : flat.substring(0, 200) + "…";
    }

    private static String describe(Throwable cause) {
        if (cause == null) {
            return "no further detail";
        }
        String message = cause.getMessage();
        return message == null || message.isBlank()
            ? cause.getClass().getSimpleName()
            : cause.getClass().getSimpleName() + ": " + message;
    }
}
