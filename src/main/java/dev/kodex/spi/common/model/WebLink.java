package dev.kodex.spi.common.model;

/**
 * An external link associated with a book or series.
 *
 * @param label display label
 * @param url   target URL
 */
public record WebLink(String label, String url) {
}
