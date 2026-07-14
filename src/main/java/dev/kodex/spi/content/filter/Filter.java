package dev.kodex.spi.content.filter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import dev.kodex.spi.content.ContentSource;

import java.util.List;

/**
 * A single search/browse filter. {@link FilterList} carries these between the UI (which renders
 * {@link #name()} and edits {@link #state()}) and {@link ContentSource#search}.
 *
 * @param <T> the type of {@link #state()}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Filter.Header.class, name = "header"),
    @JsonSubTypes.Type(value = Filter.Separator.class, name = "separator"),
    @JsonSubTypes.Type(value = Filter.TextFilter.class, name = "text"),
    @JsonSubTypes.Type(value = Filter.CheckBox.class, name = "checkbox"),
    @JsonSubTypes.Type(value = Filter.TriState.class, name = "tristate"),
    @JsonSubTypes.Type(value = Filter.Select.class, name = "select"),
    @JsonSubTypes.Type(value = Filter.Group.class, name = "group"),
    @JsonSubTypes.Type(value = Filter.Sort.class, name = "sort"),
})
public sealed interface Filter<T> {

    /** Display label for the UI. */
    String name();

    /** Current value: the user's selection (from the UI) or the source's default (from {@code getFilterList()}). */
    T state();

    /** A non-interactive section label. */
    record Header(String name) implements Filter<Void> {
        @Override
        public Void state() {
            return null;
        }
    }

    /** A non-interactive visual divider. */
    record Separator(String name) implements Filter<Void> {
        @Override
        public Void state() {
            return null;
        }
    }

    /** A free-text input. */
    record TextFilter(String name, String state) implements Filter<String> {
        public TextFilter(String name) {
            this(name, "");
        }

        public TextFilter {
            state = state == null ? "" : state;
        }
    }

    /** A boolean toggle. */
    record CheckBox(String name, Boolean state) implements Filter<Boolean> {
        public CheckBox(String name) {
            this(name, false);
        }

        public CheckBox {
            state = state == null ? Boolean.FALSE : state;
        }
    }

    /** A three-state checkbox: ignore / include / exclude. */
    record TriState(String name, Integer state) implements Filter<Integer> {
        public static final int STATE_IGNORE = 0;
        public static final int STATE_INCLUDE = 1;
        public static final int STATE_EXCLUDE = 2;

        public TriState(String name) {
            this(name, STATE_IGNORE);
        }

        public TriState {
            state = state == null ? STATE_IGNORE : state;
        }

        public boolean isIgnored() {
            return state == STATE_IGNORE;
        }

        public boolean isIncluded() {
            return state == STATE_INCLUDE;
        }

        public boolean isExcluded() {
            return state == STATE_EXCLUDE;
        }
    }

    /** A single-choice dropdown; {@code state} is the selected index into {@code values}. */
    record Select(String name, List<String> values, Integer state) implements Filter<Integer> {
        public Select(String name, List<String> values) {
            this(name, values, 0);
        }

        public Select {
            values = values == null ? List.of() : List.copyOf(values);
            state = state == null ? 0 : state;
        }
    }

    /** A group of nested filters (e.g. a multi-select list of {@link CheckBox}/{@link TriState} options). */
    record Group(String name, List<Filter<?>> state) implements Filter<List<Filter<?>>> {
        public Group {
            state = state == null ? List.of() : List.copyOf(state);
        }
    }

    /** A sort key picker with optional ascending/descending direction. */
    record Sort(String name, List<String> values, Selection state) implements Filter<Sort.Selection> {
        public Sort(String name, List<String> values) {
            this(name, values, null);
        }

        public Sort {
            values = values == null ? List.of() : List.copyOf(values);
        }

        public record Selection(int index, boolean ascending) {
        }
    }
}
