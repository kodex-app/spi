package dev.kodex.spi.content;

import java.util.List;

/**
 * One page of a browse/search feed (Tachiyomi's {@code MangasPage}): a slice of series plus whether
 * another page exists, so the UI can paginate / infinite-scroll.
 *
 * @param items       series found on this page
 * @param hasNextPage whether a further page can be fetched
 */
public record SeriesPage(List<SearchResult> items, boolean hasNextPage) {
    public SeriesPage {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public static SeriesPage empty() {
        return new SeriesPage(List.of(), false);
    }
}
