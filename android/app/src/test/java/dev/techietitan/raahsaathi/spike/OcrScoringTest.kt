package dev.techietitan.raahsaathi.spike

import org.junit.Assert.assertEquals
import org.junit.Test

class OcrScoringTest {
    @Test
    fun normalizeStripsPunctuationAndCase() {
        assertEquals("cultural", OcrScoring.normalize("CULTURAL,"))
    }

    @Test
    fun normalizeKeepsDevanagariMatras() {
        assertEquals("राहसाथी", OcrScoring.normalize("राहसाथी!"))
    }

    @Test
    fun tokensSplitOnWhitespaceAndDropEmpty() {
        assertEquals(listOf("main", "road"), OcrScoring.tokens("Main  - Road\n"))
    }

    @Test
    fun editDistanceBasics() {
        assertEquals(0, OcrScoring.editDistance("road", "road"))
        assertEquals(1, OcrScoring.editDistance("road", "roal"))
        assertEquals(4, OcrScoring.editDistance("", "road"))
    }

    @Test
    fun bestCerPicksClosestToken() {
        assertEquals(0.0, OcrScoring.bestCer("road", listOf("main", "road")), 1e-9)
        assertEquals(0.25, OcrScoring.bestCer("road", listOf("roal")), 1e-9)
        assertEquals(1.0, OcrScoring.bestCer("road", emptyList()), 1e-9)
    }
}
