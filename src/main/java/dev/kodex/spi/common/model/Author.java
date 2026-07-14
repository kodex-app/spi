package dev.kodex.spi.common.model;

/**
 * A contributor to a book or series.
 *
 * @param name name of the person
 * @param role role, lowercase (e.g. {@code "writer"}, {@code "penciller"}, {@code "translator"})
 */
public record Author(String name, String role) {
}
