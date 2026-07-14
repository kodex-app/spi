package dev.kodex.spi;

import java.util.Map;
import java.util.Optional;

/**
 * Resolved configuration values handed to an extension at call time. Keys correspond to
 * {@link PluginConfigSchema.Field#key()}.
 */
public record ProviderSettings(Map<String, String> values) {

    public ProviderSettings {
        values = Map.copyOf(values);
    }

    public static ProviderSettings empty() {
        return new ProviderSettings(Map.of());
    }

    public Optional<String> getString(String key) {
        return Optional.ofNullable(values.get(key)).filter(v -> !v.isBlank());
    }

    public String getString(String key, String defaultValue) {
        return getString(key).orElse(defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getString(key).map(Boolean::parseBoolean).orElse(defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return getString(key).map(v -> {
            try {
                return Integer.parseInt(v.trim());
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }).orElse(defaultValue);
    }
}
