package dev.kodex.spi;

import dev.kodex.spi.common.http.HttpClientProvider;
import org.pf4j.ExtensionPoint;

/**
 * Common contract for all Kodex extension points (metadata and download providers).
 *
 * <p>Implementations are discovered by PF4J via the {@code @Extension} annotation and exposed
 * to the core app through the provider registries.
 */
public interface KodexExtension extends ExtensionPoint {

    /** Stable, unique identifier for this extension (e.g. {@code "comicinfo"}). Used as a config key. */
    String id();

    /** Human-readable name shown in the UI. */
    String displayName();

    /**
     * Declares the configuration fields this extension accepts. The UI renders a settings form
     * from this schema, and resolved values are passed back as {@link ProviderSettings}.
     */
    default PluginConfigSchema configSchema() {
        return PluginConfigSchema.empty();
    }

    /**
     * Whether this extension surfaces adult (18+) content. The host hides such extensions and blocks
     * their use unless an administrator has opted in. Default {@code false} (safe for work).
     */
    default boolean adultContent() {
        return false;
    }

    /**
     * Injected by the host before the extension is used, giving it the centrally-configured HTTP client
     * (proxy + DoH). Extensions that make outbound requests should keep the provider and fetch a fresh
     * client per request via {@link HttpClientProvider#httpClient()}. Default is a no-op for extensions
     * that don't need network access (e.g. local-file metadata parsers).
     */
    default void setHttpClientProvider(HttpClientProvider provider) {
    }
}
