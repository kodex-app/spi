package dev.kodex.spi.metadata;

import java.nio.file.Path;
import java.util.Map;

/**
 * Read-only view of a series passed to a {@link MetadataProvider}.
 *
 * @param seriesId    stable id of the series
 * @param name        series folder name
 * @param folderPath  absolute path to the series directory
 * @param bookCount   number of books currently known in the series
 * @param identifiers external identifiers already known for this series, keyed by
 *                    {@link MetadataIdentifiers} (e.g. {@code amazon}, {@code googlebooks}); never
 *                    {@code null}. Providers should prefer an ID-based lookup over a name search when a
 *                    relevant key is present.
 */
public record SeriesContext(
    String seriesId,
    String name,
    Path folderPath,
    int bookCount,
    Map<String, String> identifiers
) {
    public SeriesContext {
        identifiers = identifiers == null ? Map.of() : Map.copyOf(identifiers);
    }
}
