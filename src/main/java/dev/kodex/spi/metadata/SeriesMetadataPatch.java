package dev.kodex.spi.metadata;

import dev.kodex.spi.common.model.WebLink;

import java.util.List;
import java.util.Map;

/**
 * A set of series metadata fields produced by a {@link MetadataProvider}. Every field is nullable:
 * {@code null} means "no opinion" and the core keeps the existing value.
 */
public record SeriesMetadataPatch(
    String title,
    String titleSort,
    String summary,
    String status,
    String publisher,
    String ageRating,
    String language,
    String readingDirection,
    Integer totalBookCount,
    List<String> genres,
    List<String> tags,
    List<WebLink> links,
    String coverUrl,
    Map<String, String> identifiers
) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String title;
        private String titleSort;
        private String summary;
        private String status;
        private String publisher;
        private String ageRating;
        private String language;
        private String readingDirection;
        private Integer totalBookCount;
        private List<String> genres;
        private List<String> tags;
        private List<WebLink> links;
        private String coverUrl;
        private Map<String, String> identifiers;

        public Builder title(String v) { this.title = v; return this; }
        public Builder titleSort(String v) { this.titleSort = v; return this; }
        public Builder summary(String v) { this.summary = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder publisher(String v) { this.publisher = v; return this; }
        public Builder ageRating(String v) { this.ageRating = v; return this; }
        public Builder language(String v) { this.language = v; return this; }
        public Builder readingDirection(String v) { this.readingDirection = v; return this; }
        public Builder totalBookCount(Integer v) { this.totalBookCount = v; return this; }
        public Builder genres(List<String> v) { this.genres = v == null ? null : List.copyOf(v); return this; }
        public Builder tags(List<String> v) { this.tags = v == null ? null : List.copyOf(v); return this; }
        public Builder links(List<WebLink> v) { this.links = v == null ? null : List.copyOf(v); return this; }
        public Builder coverUrl(String v) { this.coverUrl = v; return this; }
        public Builder identifiers(Map<String, String> v) { this.identifiers = v == null ? null : Map.copyOf(v); return this; }

        public SeriesMetadataPatch build() {
            return new SeriesMetadataPatch(title, titleSort, summary, status, publisher, ageRating,
                language, readingDirection, totalBookCount, genres, tags, links, coverUrl, identifiers);
        }
    }
}
