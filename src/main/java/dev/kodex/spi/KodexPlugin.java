package dev.kodex.spi;

import org.pf4j.Plugin;

/**
 * Base class for every Kodex plugin.
 *
 * <p>A plugin is packaged as a self-contained JAR loaded by PF4J into its own classloader.
 * It contributes one or more {@link KodexExtension}s (metadata or download providers).
 * Override {@link #start()} / {@link #stop()} for lifecycle work (open/close clients, etc.).
 */
public abstract class KodexPlugin extends Plugin {
}
