package dev.kodex.spi.content;

import java.util.Map;

/**
 * One page of a chapter as reported by {@link ContentSource#pageList}. The source provides only the
 * image <em>location</em> (and any headers needed to fetch it, e.g. {@code Referer}); the
 * <strong>core</strong> performs the actual download or on-demand streaming — sources never write to
 * disk. {@code imageUrl} is typically {@code http(s)}; a {@code data:} URI is also supported (handy
 * for offline/test sources).
 *
 * @param index    0-based page index within the chapter
 * @param imageUrl absolute image URL (http/https) or a {@code data:} URI
 * @param headers  request headers the core must send when fetching {@code imageUrl} (may be empty)
 */
public record SourcePage(int index, String imageUrl, Map<String, String> headers) {
    public SourcePage {
        headers = headers == null ? Map.of() : Map.copyOf(headers);
    }

    public SourcePage(int index, String imageUrl) {
        this(index, imageUrl, Map.of());
    }
}
