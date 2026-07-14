package dev.kodex.spi.content.filter;

import java.util.List;

/**
 * An ordered list of {@link Filter}s. The core sends
 * the source's default list (from {@code getFilterList()}) to the UI; the UI sends back the same
 * list with user-edited {@link Filter#state()} values to {@code search()}.
 */
public record FilterList(List<Filter<?>> filters) {
    public FilterList {
        filters = filters == null ? List.of() : List.copyOf(filters);
    }

    public static FilterList empty() {
        return new FilterList(List.of());
    }

    public static FilterList of(Filter<?>... filters) {
        return new FilterList(List.of(filters));
    }
}
