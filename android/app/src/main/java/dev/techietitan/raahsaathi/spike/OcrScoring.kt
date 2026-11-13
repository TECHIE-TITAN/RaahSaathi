package dev.techietitan.raahsaathi.spike

import java.text.Normalizer

/** Text matching used by the OCR spike (step 1.4). Pure Kotlin so it can be unit tested. */
object OcrScoring {
    private val nonWord = Regex("[^\\p{L}\\p{M}\\p{N}]")
    private val whitespace = Regex("\\s+")

    /** NFC, lower case, letters/marks/digits only. Marks are kept so Devanagari matras survive. */
    fun normalize(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFC).lowercase().replace(nonWord, "")

    fun tokens(text: String): List<String> =
        text.split(whitespace).map(::normalize).filter { it.isNotEmpty() }

    fun editDistance(a: String, b: String): Int {
        if (a == b) return 0
        if (a.isEmpty()) return b.length
        if (b.isEmpty()) return a.length
        var prev = IntArray(b.length + 1) { it }
        var cur = IntArray(b.length + 1)
        for (i in 1..a.length) {
            cur[0] = i
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                cur[j] = minOf(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + cost)
            }
            val t = prev; prev = cur; cur = t
        }
        return prev[b.length]
    }

    /** Edit distance of the closest recognised token, divided by the word length, capped at 1. */
    fun bestCer(word: String, tokens: Collection<String>): Double {
        if (word.isEmpty() || tokens.isEmpty()) return 1.0
        var best = Int.MAX_VALUE
        for (t in tokens) {
            val d = editDistance(word, t)
            if (d < best) best = d
            if (best == 0) break
        }
        return minOf(1.0, best.toDouble() / word.length)
    }
}
