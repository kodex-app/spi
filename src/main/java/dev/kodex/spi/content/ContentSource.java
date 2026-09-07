package dev.kodex.spi.content;

import dev.kodex.spi.KodexExtension;
import dev.kodex.spi.MediaKind;
import dev.kodex.spi.ProviderSettings;
import dev.kodex.spi.common.http.ProviderRateLimitException;
import dev.kodex.spi.common.http.SourceUnavailableException;
import dev.kodex.spi.content.filter.FilterList;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A content source (Tachiyomi-like): browses, searches, describes, and lists the pages of series
 * from an external site. Plugins implement this and annotate the implementation with PF4J's
 * {@code @Extension}.
 *
 * <p><b>The source never downloads files.</b> It only tells the core <em>what</em> exists and
 * <em>where</em> the images are ({@link #pageList} → {@link SourcePage} URLs + headers); the core
 * does the fetching — either streaming pages on demand for reading, or downloading them into a WEB
 * library. This keeps storage/throttling/caching policy in the core, not in every plugin.
 *
 * <p>Flow: browse ({@link #popular}/{@link #latest}) or {@link #search} → user adds a series to a WEB
 * library → {@link #listChapters} drives updates/auto-download → {@link #pageList} yields the images
 * to read or download.
 *
 * <p><b>Report failures, don't return empty.</b> An empty result means "the site has nothing here",
 * and nothing else — it is what the apps render as an empty shelf. A request that failed is not that,
 * so throw:
 * <ul>
 *   <li>{@link SourceUnavailableException} when the upstream could not be reached, answered non-2xx,
 *       or sent a body this source cannot parse. The core turns it into a 502 carrying the message,
 *       so the user sees <em>why</em> the source came up blank instead of "nothing to show here".</li>
 *   <li>{@link ProviderRateLimitException} for an upstream rate limit (HTTP 429), so the core can back
 *       off and retry — a download waits out the limit instead of failing the chapter.</li>
 * </ul>
 *
 * <p>Throwing is safe on every path: a library refresh logs and skips the series, and a download job
 * fails only itself. Genuinely empty answers (a search with no matches, a series with no chapters yet)
 * still return an empty result — that is the one case an empty result is honest about.
 */
public interface ContentSource extends KodexExtension {

    /**
     * The kind of content this source serves. The host only lets a source be bound to a WEB library of
     * the same {@link MediaKind}, and uses it to pick the reader/player. Defaults to {@link MediaKind#COMIC}
     * — the page-image model the rest of this interface ({@link #pageList}) assumes; novel/video sources
     * override it (and will gain kind-specific content methods in a future SPI revision).
     */
    default MediaKind kind() {
        return MediaKind.COMIC;
    }

    /**
     * Bump it only if the upstream site change makes stored urls incompatible.
     * Defaults to {@code 1}.
     */
    default int versionId() {
        return 1;
    }

    /**
     * The source's id: the first 8 bytes of
     * {@code MD5("{name.toLowerCase}/{lang}/{versionId}")} read big-endian with the sign bit cleared
     * (mirrors {@code HttpSource.generateId}), from {@link #displayName()}, {@link #language()} (or
     * {@code "all"} when mixed-language), and {@link #versionId()}.
     */
    @Override
    default String id() {
        String lang = language() == null ? "all" : language();
        return Long.toString(calcId(displayName(), lang, versionId()));
    }

    /** A page of the source's popular/most-followed series. {@code page} is 1-based. */
    SeriesPage popular(int page, ProviderSettings settings);

    /** A page of the source's latest updates. Sources without a latest feed should set {@link #supportsLatest()} false. */
    default SeriesPage latest(int page, ProviderSettings settings) {
        return SeriesPage.empty();
    }

    /** A page of search results for {@code query}, refined by {@code filters}. {@code page} is 1-based. */
    SeriesPage search(String query, int page, FilterList filters, ProviderSettings settings);

    /**
     * The filters available for {@link #search}. The UI renders these and sends back user-edited state; sources
     * with no filters return {@link FilterList#empty()} (the default).
     */
    default FilterList getFilterList() {
        return FilterList.empty();
    }

    /** Whether {@link #latest} is supported (drives the UI's "Latest" tab). */
    default boolean supportsLatest() {
        return true;
    }

    /**
     * The source's website URL (e.g. {@code https://mangadex.org}), used by the UI to show a favicon
     * and link out. Return {@code null} (default) if the source has no meaningful site.
     */
    default String website() {
        return null;
    }

    /**
     * The IETF/BCP-47 language tag (e.g. {@code "en"}, {@code "ja"}) of this source's content. Series
     * followed from this source get this as their {@code SeriesMetadata} language by default. Return
     * {@code null} (default) if the source serves mixed-language content.
     */
    default String language() {
        return null;
    }

    /** Up-to-date details for a series (refreshes metadata when following / updating a WEB library). */
    SearchResult seriesDetails(String seriesExternalId, ProviderSettings settings);

    /** The chapters/volumes available for a series, so the core can detect new content. */
    List<SourceChapter> listChapters(String seriesExternalId, ProviderSettings settings);

    /**
     * The ordered page images of a chapter. The core fetches each {@link SourcePage#imageUrl}. This is the
     * {@link MediaKind#COMIC} content path; {@link MediaKind#BOOK} sources return an empty list here and
     * implement {@link #chapterContent} instead.
     */
    List<SourcePage> pageList(String chapterExternalId, ProviderSettings settings);

    /**
     * The text content of a chapter, for {@link MediaKind#BOOK} sources (the text counterpart of
     * {@link #pageList}). The core packages the returned HTML into an EPUB to download or stream — the
     * source never writes to disk. COMIC sources don't override this (the default throws); BOOK sources
     * must, and should return an empty {@link #pageList}. A chapter that could not be fetched throws
     * {@link SourceUnavailableException} rather than returning empty html — an empty chapter would be
     * downloaded, imported, and cached as a blank read — and an upstream rate limit throws
     * {@link ProviderRateLimitException} so the core waits and retries the download.
     */
    default SourceChapterContent chapterContent(String chapterExternalId, ProviderSettings settings) {
        throw new UnsupportedOperationException("chapterContent is only supported by BOOK sources");
    }

    /**
     * Extra HTTP headers the core applies when fetching this source's <em>cover</em> images (page images
     * carry their own headers via {@link SourcePage}). These override the core's defaults per key — e.g. a
     * source whose CDN rejects spoofed browser User-Agents returns an honest one here. Empty by default,
     * so the core's standard browser-like headers (User-Agent + {@link #website()} Referer) are used.
     */
    default Map<String, String> coverRequestHeaders() {
        return Map.of();
    }

    /**
     * Maps a Mihon/Tachiyomi {@code BackupManga.url} to this source's {@code seriesExternalId}, used when
     * importing a {@code .tachibk} backup. Defaults to the url unchanged — override when Mihon stored a path
     * but the source keys series by something else (e.g. the last path segment of a {@code /title/<id>} url).
     */
    default String toSeriesExternalId(String mihonMangaUrl) {
        return mihonMangaUrl;
    }

    /** Maps a Mihon {@code BackupChapter.url} to the {@code externalId} this source's {@link #listChapters} emits. */
    default String toChapterExternalId(String mihonChapterUrl) {
        return mihonChapterUrl;
    }

    /**
     * The first 8 bytes of {@code MD5("{name.toLowerCase}/{lang}/{versionId}")} read big-endian, sign bit cleared.
     */
    static long calcId(String name, String lang, int versionId) {
        // Kotlin's String.lowercase() is locale-invariant — Locale.ROOT matches it.
        String key = name.toLowerCase(Locale.ROOT) + "/" + lang + "/" + versionId;
        byte[] bytes;
        try {
            bytes = MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 is required", e); // never on a JRE
        }
        long id = 0;
        for (int i = 0; i < 8; i++) {
            id |= (bytes[i] & 0xffL) << (8 * (7 - i));
        }
        return id & Long.MAX_VALUE;
    }
}
