package dev.kodex.spi;

import java.util.List;

/**
 * Declarative description of the configuration an extension accepts. Rendered by the UI as a form;
 * the saved values are handed back to the extension as {@link ProviderSettings}.
 */
public record PluginConfigSchema(List<Field> fields) {

    public PluginConfigSchema {
        fields = List.copyOf(fields);
    }

    public static PluginConfigSchema empty() {
        return new PluginConfigSchema(List.of());
    }

    public enum FieldType {
        STRING,
        SECRET,   // stored encrypted, masked in the UI
        BOOLEAN,
        INTEGER,
        ENUM
    }

    /**
     * A single configuration field.
     *
     * @param key          stable key used to read the value from {@link ProviderSettings}
     * @param label        UI label
     * @param type         input type
     * @param required     whether a value must be provided
     * @param defaultValue default value as a string, or {@code null}
     * @param options      allowed values when {@code type == ENUM}, otherwise empty
     */
    public record Field(
        String key,
        String label,
        FieldType type,
        boolean required,
        String defaultValue,
        List<String> options
    ) {
        public Field {
            options = options == null ? List.of() : List.copyOf(options);
        }

        public static Field string(String key, String label, boolean required) {
            return new Field(key, label, FieldType.STRING, required, null, List.of());
        }

        public static Field secret(String key, String label, boolean required) {
            return new Field(key, label, FieldType.SECRET, required, null, List.of());
        }

        public static Field bool(String key, String label, boolean defaultValue) {
            return new Field(key, label, FieldType.BOOLEAN, false, Boolean.toString(defaultValue), List.of());
        }
    }
}
