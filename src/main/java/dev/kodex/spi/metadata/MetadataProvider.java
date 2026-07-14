package dev.kodex.spi.metadata;

import dev.kodex.spi.KodexExtension;
import dev.kodex.spi.ProviderSettings;

import java.util.Optional;
import java.util.Set;

/**
 * Extension point that supplies metadata for series and/or books. Plugins implement this and
 * annotate the implementation with PF4J's {@code @Extension}.
 *
 * <p>Providers return {@link Optional} patches; the core merges patches from all enabled providers
 * (respecting per-library priority and field locks) during a metadata-refresh task.
 */
public interface MetadataProvider extends KodexExtension {

    /** The set of fields this provider can populate. */
    Set<MetadataCapability> capabilities();

    /** Whether this provider participates in refreshes for the given target type. */
    boolean supports(MetadataTarget target);

    /** Produce series-level metadata, or empty if this provider has nothing to contribute. */
    default Optional<SeriesMetadataPatch> seriesMetadata(SeriesContext context, ProviderSettings settings) {
        return Optional.empty();
    }

    /** Produce book-level metadata, or empty if this provider has nothing to contribute. */
    default Optional<BookMetadataPatch> bookMetadata(BookContext context, ProviderSettings settings) {
        return Optional.empty();
    }
}
