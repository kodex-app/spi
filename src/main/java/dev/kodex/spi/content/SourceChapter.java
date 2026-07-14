package dev.kodex.spi.content;

import java.time.LocalDate;
import java.util.Map;

/**
 * A chapter/volume of a series as reported by a content source's {@link ContentSource#listChapters}.
 * The core diffs this list against the books it already has to decide what's new (the WEB-library
 * "scan = fetch updates" flow), then downloads the missing ones.
 *
 * @param externalId  provider-specific id, passed back to {@link ContentSource#pageList} to fetch it
 * @param name        display name (e.g. "Chapter 12: ...")
 * @param number      chapter/volume number, if known (may be {@code null})
 * @param scanlator   scanlation group(s), if known (may be {@code null})
 * @param releaseDate publication date, if known (may be {@code null})
 * @param attributes  free-form provider metadata (e.g. group, language)
 */
public record SourceChapter(
    String externalId,
    String name,
    Double number,
    String scanlator,
    LocalDate releaseDate,
    Map<String, String> attributes
) {
    public SourceChapter {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
