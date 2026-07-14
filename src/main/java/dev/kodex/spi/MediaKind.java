package dev.kodex.spi;

/**
 * The kind of content an extension or library deals in. A {@code ContentSource} declares the kind it
 * serves so the host can route it to a matching library and render the right reader/player; a WEB library
 * holds exactly one kind. Stable across the SPI — new kinds are additive.
 */
public enum MediaKind {
    /** Page-image content read in a pager: manga, comics, manhwa, webtoons. */
    COMIC,
    /** Text content read with reflow: web novels, light novels, ebooks. */
    BOOK
}
