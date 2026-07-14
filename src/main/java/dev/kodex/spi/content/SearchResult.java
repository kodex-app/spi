package dev.kodex.spi.content;

import java.util.List;
import java.util.Map;

/**
 * A single downloadable item found by a {@link ContentSource#search}
 *
 * @param providerId  id of the provider that produced this result
 * @param externalId  provider-specific id used later to enqueue the download
 * @param title       display title
 * @param description optional description (may be {@code null})
 * @param coverUrl    optional cover image URL (may be {@code null})
 * @param author      optional author name(s) (may be {@code null})
 * @param artist      optional artist name(s) (may be {@code null})
 * @param genres      genre/tag list (empty if none)
 * @param status      publication status ({@link SeriesStatus#UNKNOWN} if not reported)
 * @param attributes  free-form provider metadata (e.g. language, size, chapter count)
 */
public record SearchResult(
    String providerId,
    String externalId,
    String title,
    String description,
    String coverUrl,
    String author,
    String artist,
    List<String> genres,
    SeriesStatus status,
    Map<String, String> attributes
) {
    public SearchResult {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        genres = genres == null ? List.of() : List.copyOf(genres);
        status = status == null ? SeriesStatus.UNKNOWN : status;
    }
}
