package dev.techietitan.raahsaathi.spike

import android.content.Context
import android.graphics.BitmapFactory
import android.os.SystemClock
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Step 1.4: run ML Kit Latin and Devanagari recognisers over the BSTD sample and score the
 * output against ground truth. Assumes dataset-style framing (ADR-017).
 *
 * Input:  <filesDir>/bstd_sample/ containing sample.json and an images folder of jpg files
 * Output: <filesDir>/ocr_results.json
 */
class OcrSpike(private val context: Context) {

    class Stat {
        var n = 0
        var exact = 0
        var soft = 0
        var cerSum = 0.0
        fun toJson() = JSONObject()
            .put("words", n)
            .put("exact_recall", if (n == 0) 0.0 else exact.toDouble() / n)
            .put("soft_recall_cer_le_0.25", if (n == 0) 0.0 else soft.toDouble() / n)
            .put("mean_best_cer", if (n == 0) 0.0 else cerSum / n)
    }

    // Private storage: the app owns these files, so there are no scoped-storage surprises.
    // Use tools/push_sample.sh and tools/pull_results.sh to move data in and out.
    val sampleDir: File get() = File(context.filesDir, "bstd_sample")
    val resultsFile: File get() = File(context.filesDir, "ocr_results.json")

    suspend fun run(onProgress: (done: Int, total: Int) -> Unit): String {
        val sampleJson = File(sampleDir, "sample.json")
        require(sampleJson.exists()) { "Missing ${sampleJson.absolutePath}" }
        val images = JSONObject(sampleJson.readText()).getJSONArray("images")

        val latin = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val devanagari = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())

        val stats = sortedMapOf<String, Stat>()
        val latinMs = ArrayList<Long>()
        val devMs = ArrayList<Long>()
        val perImage = JSONArray()
        val total = images.length()

        // Warm-up so model loading is not counted in the timings.
        File(sampleDir, "images/" + images.getJSONObject(0).getString("file")).let { f ->
            BitmapFactory.decodeFile(f.path)?.let { bmp ->
                latin.process(InputImage.fromBitmap(bmp, 0)).await()
                devanagari.process(InputImage.fromBitmap(bmp, 0)).await()
                bmp.recycle()
            }
        }

        for (i in 0 until total) {
            val rec = images.getJSONObject(i)
            val bmp = BitmapFactory.decodeFile(File(sampleDir, "images/" + rec.getString("file")).path)
            if (bmp == null) {
                onProgress(i + 1, total)
                continue
            }
            val input = InputImage.fromBitmap(bmp, 0)

            var t0 = SystemClock.elapsedRealtime()
            val latinText = latin.process(input).await().text
            val lMs = SystemClock.elapsedRealtime() - t0
            t0 = SystemClock.elapsedRealtime()
            val devText = devanagari.process(input).await().text
            val dMs = SystemClock.elapsedRealtime() - t0
            bmp.recycle()
            latinMs += lMs
            devMs += dMs

            val latinTokens = OcrScoring.tokens(latinText).toSet()
            val devTokens = OcrScoring.tokens(devText).toSet()
            val tokenSets = mapOf(
                "latin" to latinTokens,
                "devanagari" to devTokens,
                "union" to (latinTokens + devTokens),
            )

            val env = rec.optString("environment", "unknown")
            val light = rec.optString("lighting", "unknown")
            val words = rec.getJSONArray("words")
            for (w in 0 until words.length()) {
                val word = words.getJSONObject(w)
                val script = word.getString("script")
                if (script != "hindi" && script != "english") continue
                val gt = OcrScoring.normalize(word.getString("text"))
                if (gt.isEmpty()) continue
                for ((name, tokens) in tokenSets) {
                    val cer = OcrScoring.bestCer(gt, tokens)
                    for (slice in listOf("all", "env:$env", "light:$light")) {
                        val s = stats.getOrPut("$name|$script|$slice") { Stat() }
                        s.n++
                        if (cer == 0.0) s.exact++
                        if (cer <= 0.25) s.soft++
                        s.cerSum += cer
                    }
                }
            }

            perImage.put(
                JSONObject()
                    .put("id", rec.getString("id"))
                    .put("latin_ms", lMs)
                    .put("devanagari_ms", dMs)
                    .put("latin_text", latinText)
                    .put("devanagari_text", devText),
            )
            onProgress(i + 1, total)
        }
        latin.close()
        devanagari.close()

        val statsJson = JSONObject()
        stats.forEach { (k, v) -> statsJson.put(k, v.toJson()) }
        val out = JSONObject()
            .put("images", total)
            .put("timing_ms", JSONObject().put("latin", timing(latinMs)).put("devanagari", timing(devMs)))
            .put("stats", statsJson)
            .put("per_image", perImage)
        resultsFile.writeText(out.toString(2))

        return summary(stats, latinMs, devMs, total)
    }

    private fun timing(xs: List<Long>): JSONObject {
        if (xs.isEmpty()) return JSONObject()
        val s = xs.sorted()
        return JSONObject()
            .put("median", s[s.size / 2])
            .put("p90", s[(s.size * 9) / 10])
            .put("max", s.last())
    }

    private fun summary(stats: Map<String, Stat>, latinMs: List<Long>, devMs: List<Long>, total: Int): String {
        fun line(rec: String, script: String): String {
            val s = stats["$rec|$script|all"] ?: return "$rec $script: no data"
            val j = s.toJson()
            return "%-10s %-7s n=%d exact=%.0f%% soft=%.0f%% cer=%.2f".format(
                rec, script, s.n,
                j.getDouble("exact_recall") * 100,
                j.getDouble("soft_recall_cer_le_0.25") * 100,
                j.getDouble("mean_best_cer"),
            )
        }
        val ls = latinMs.sorted()
        val ds = devMs.sorted()
        return buildString {
            appendLine("Images: $total")
            for (script in listOf("english", "hindi")) for (rec in listOf("latin", "devanagari", "union")) appendLine(line(rec, script))
            if (ls.isNotEmpty()) appendLine("Latin ms: median ${ls[ls.size / 2]}, p90 ${ls[ls.size * 9 / 10]}")
            if (ds.isNotEmpty()) appendLine("Devanagari ms: median ${ds[ds.size / 2]}, p90 ${ds[ds.size * 9 / 10]}")
            appendLine("Saved: ${resultsFile.absolutePath}")
        }
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }
}
