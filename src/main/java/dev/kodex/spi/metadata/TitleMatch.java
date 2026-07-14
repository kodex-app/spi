package dev.kodex.spi.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Title-relevance scoring for metadata search results (ported from bookorbit's candidate-relevance
 * ranking). Name-based searches on volume stores routinely return a "best effort" first hit that has
 * nothing to do with the series — e.g. "How to Draw Manga Characters" for the series "A Guide for
 * Background Characters to Survive in a Manga". Providers score each candidate's title against the
 * searched name and reject everything below {@link #DEFAULT_THRESHOLD} instead of trusting result order.
 *
 * <p>Scores: exact (normalized) match 10 · prefix either way 8 · containment either way 7 · otherwise
 * the best of token-overlap (×6) and Levenshtein similarity (×4, only when ≥ 0.6). Real matches — the
 * series name plus a volume/edition suffix — land at 7+; unrelated titles sharing a couple of words
 * stay under 4, so the default threshold of 5 separates them cleanly.
 */
public final class TitleMatch {

    /** Minimum {@link #score} for a candidate to be considered the searched series/book at all. */
    public static final double DEFAULT_THRESHOLD = 5.0;

    private TitleMatch() {
    }

    /** True when {@code candidateTitle} plausibly is the work named {@code query} (score ≥ threshold). */
    public static boolean acceptable(String query, String candidateTitle) {
        return score(query, candidateTitle) >= DEFAULT_THRESHOLD;
    }

    /** Relevance of a candidate title to the searched name, 0 (unrelated) to 10 (exact). */
    public static double score(String query, String candidateTitle) {
        if (query == null || candidateTitle == null) {
            return 0;
        }
        String q = normalize(query);
        String c = normalize(candidateTitle);
        if (q.isEmpty() || c.isEmpty()) {
            return 0;
        }
        if (c.equals(q)) {
            return 10;
        }
        if (c.startsWith(q) || q.startsWith(c)) {
            return 8;
        }
        if (c.contains(q) || q.contains(c)) {
            return 7;
        }
        List<String> queryTokens = tokenize(q);
        Set<String> candidateTokens = new HashSet<>(tokenize(c));
        double overlap = queryTokens.isEmpty() ? 0
            : queryTokens.stream().filter(candidateTokens::contains).count() / (double) queryTokens.size();
        double tokenScore = overlap * 6;

        double similarity = levenshteinSimilarity(q, c);
        double levenScore = similarity >= 0.6 ? similarity * 4 : 0;

        return Math.max(tokenScore, levenScore);
    }

    /** Lowercased with everything but letters/digits collapsed to single spaces (Unicode-aware). */
    public static String normalize(String s) {
        return s.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{Nd}]+", " ").trim();
    }

    private static List<String> tokenize(String normalized) {
        List<String> tokens = new ArrayList<>();
        for (String token : normalized.split(" ")) {
            if (token.length() > 1) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    /** 1 − normalizedEditDistance: 1 for identical strings, towards 0 as they diverge. */
    private static double levenshteinSimilarity(String a, String b) {
        int maxLen = Math.max(a.length(), b.length());
        return maxLen == 0 ? 1 : 1 - levenshtein(a, b) / (double) maxLen;
    }

    private static int levenshtein(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] curr = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            curr[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                curr[j] = Math.min(Math.min(curr[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] swap = prev;
            prev = curr;
            curr = swap;
        }
        return prev[b.length()];
    }
}
