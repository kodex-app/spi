# kodex-spi

The stable plugin SPI for [Kodex](https://github.com/kodex-app/kodex) — a plugin-extensible,
Komga-like media server. Metadata providers and content sources are PF4J plugins that compile
against this contract (`dev.kodex:kodex-spi`) and nothing else from the Kodex internals.

## What's in the contract

- `dev.kodex.spi` — `KodexPlugin`, `KodexExtension`, plugin config schema, provider settings
- `dev.kodex.spi.metadata` — `MetadataProvider` extension point + metadata patch DTOs
- `dev.kodex.spi.download` — `ContentSource` extension point, search/chapter/page DTOs, filters
- `dev.kodex.spi.common.http` — `HttpClientProvider` (the host hands plugins a configured OkHttpClient)
- `dev.kodex.spi.common.model` — shared value types (`Author`, `WebLink`)

Deliberately dependency-light: **no Spring, no JPA**. The only API-surface dependencies are PF4J,
OkHttp, and Jackson databind — all provided by the host at runtime.

## Building

```bash
./gradlew build                  # compile + jar (sources & javadoc jars included)
./gradlew publishToMavenLocal    # publish dev.kodex:kodex-spi to the local Maven cache
```

Requires a JDK 25 toolchain (Gradle resolves one automatically).

## Consuming

Published via [JitPack](https://jitpack.io/#kodex-app/kodex-spi) — every git tag is built on
demand. Third-party plugin authors:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.kodex-app:spi:<tag>")
}
```

## Versioning & releasing

Pre-1.0: the contract is still moving. The default version lives in `build.gradle.kts`
(`-PspiVersion=<version>` overrides it locally). To release, push a git tag — JitPack builds
it on first request and serves it as `com.github.kodex-app:spi:<tag>`.

## License

[GPL-3.0](LICENSE).
