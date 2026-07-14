package dev.kodex.spi.content;

import dev.kodex.spi.MediaKind;

/**
 * The text content of one chapter of a {@link MediaKind#BOOK} source, as reported by
 * {@link ContentSource#chapterContent}. This is the text counterpart of {@link SourcePage} (which models
 * a page <em>image</em> for {@link MediaKind#COMIC} sources): a BOOK source returns the
 * chapter's body as HTML and the <b>core</b> packages it (e.g. into an EPUB) and stores/serves it — the
 * source never writes to disk.
 *
 * <p>{@code html} is a fragment of the chapter body (the prose, with light structural markup such as
 * {@code <p>}, {@code <br>}, {@code <em>}); it is not a full HTML document — the core wraps it in a valid
 * XHTML skeleton. Sources should strip site chrome (nav, ads, comment sections) and any anti-scrape hidden
 * text. {@code title} is the chapter heading shown at the top of the rendered chapter (may be {@code null}).
 */
public record SourceChapterContent(String title, String html) {

    public SourceChapterContent {
        html = html == null ? "" : html;
    }
}
