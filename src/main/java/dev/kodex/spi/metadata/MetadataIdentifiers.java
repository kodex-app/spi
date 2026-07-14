package dev.kodex.spi.metadata;

/**
 * Canonical keys for external identifiers carried in the {@code identifiers} maps on
 * {@link BookContext}, {@link SeriesContext}, {@link BookMetadataPatch} and {@link SeriesMetadataPatch}.
 *
 * <p>Providers should read/write identifiers using these keys so the host and all plugins agree on a
 * shared vocabulary (the host derives external links and offers ID-first lookups from them). A provider
 * that already knows an item's ID for a given service emits it under the matching key so a later refresh
 * can fetch directly by ID instead of doing a fuzzy title search.
 */
public final class MetadataIdentifiers {

    /** ISBN-13/ISBN-10 (book-level). */
    public static final String ISBN = "isbn";
    /** Amazon Standard Identification Number (the Amazon product/ASIN id). */
    public static final String AMAZON = "amazon";
    /** Google Books volume id. */
    public static final String GOOGLE_BOOKS = "googlebooks";
    /** Goodreads book/work id. */
    public static final String GOODREADS = "goodreads";
    /** ComicVine volume id. */
    public static final String COMICVINE = "comicvine";
    /** RanobeDB book id. */
    public static final String RANOBEDB = "ranobedb";
    /** Hardcover book slug. */
    public static final String HARDCOVER = "hardcover";

    private MetadataIdentifiers() {
    }
}
