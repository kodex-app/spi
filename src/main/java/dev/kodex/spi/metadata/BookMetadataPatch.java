package dev.kodex.spi.metadata;

import dev.kodex.spi.common.model.Author;
import dev.kodex.spi.common.model.WebLink;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * A set of book metadata fields produced by a {@link MetadataProvider}. Every field is nullable:
 * {@code null} means "this provider has no opinion" and the core leaves the existing value alone.
 * Collections that are non-null replace the existing value.
 */
public record BookMetadataPatch(
    String title,
    String summary,
    Integer number,
    Double numberSort,
    LocalDate releaseDate,
    String isbn,
    List<Author> authors,
    List<String> tags,
    List<WebLink> links,
    Map<String, String> identifiers
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String title;
        private String summary;
        private Integer number;
        private Double numberSort;
        private LocalDate releaseDate;
        private String isbn;
        private List<Author> authors;
        private List<String> tags;
        private List<WebLink> links;
        private Map<String, String> identifiers;

        public Builder title(String v) { this.title = v; return this; }
        public Builder summary(String v) { this.summary = v; return this; }
        public Builder number(Integer v) { this.number = v; return this; }
        public Builder numberSort(Double v) { this.numberSort = v; return this; }
        public Builder releaseDate(LocalDate v) { this.releaseDate = v; return this; }
        public Builder isbn(String v) { this.isbn = v; return this; }
        public Builder authors(List<Author> v) { this.authors = v == null ? null : List.copyOf(v); return this; }
        public Builder tags(List<String> v) { this.tags = v == null ? null : List.copyOf(v); return this; }
        public Builder links(List<WebLink> v) { this.links = v == null ? null : List.copyOf(v); return this; }
        public Builder identifiers(Map<String, String> v) { this.identifiers = v == null ? null : Map.copyOf(v); return this; }

        public BookMetadataPatch build() {
            return new BookMetadataPatch(title, summary, number, numberSort, releaseDate, isbn, authors, tags,
                links, identifiers);
        }
    }
}
