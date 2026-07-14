package dev.kodex.spi.metadata;

import java.nio.file.Path;
import java.util.Map;

/**
 * Read-only view of a book passed to a {@link MetadataProvider}. Providers that read embedded
 * files (e.g. {@code ComicInfo.xml} inside a CBZ) open {@link #filePath()} themselves; this keeps
 * the core's internal media model out of the plugin contract.
 *
 * @param bookId      stable id of the book
 * @param seriesId    stable id of the owning series
 * @param name        file name without extension
 * @param filePath    absolute path to the book file
 * @param mediaType   detected media type (e.g. {@code "application/vnd.comicbook+zip"})
 * @param number      book number within the series, if known (may be {@code null})
 * @param identifiers external identifiers already known for this book, keyed by
 *                    {@link MetadataIdentifiers} (e.g. {@code isbn}, {@code amazon}); never {@code null}.
 *                    Providers should prefer an ID-based lookup over a title search when a relevant key
 *                    is present.
 */
public record BookContext(
    String bookId,
    String seriesId,
    String name,
    Path filePath,
    String mediaType,
    Integer number,
    Map<String, String> identifiers
) {
    public BookContext {
        identifiers = identifiers == null ? Map.of() : Map.copyOf(identifiers);
    }
}
