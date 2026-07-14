package dev.kodex.spi.metadata;

/**
 * Fields a {@link MetadataProvider} is able to populate. Declared up front so the core can
 * order/merge providers and let users lock specific fields.
 */
public enum MetadataCapability {
    TITLE,
    TITLE_SORT,
    SUMMARY,
    NUMBER,
    NUMBER_SORT,
    RELEASE_DATE,
    AUTHORS,
    TAGS,
    GENRES,
    PUBLISHER,
    AGE_RATING,
    LANGUAGE,
    READING_DIRECTION,
    LINKS,
    ISBN,
    IDENTIFIERS,
    THUMBNAIL
}
