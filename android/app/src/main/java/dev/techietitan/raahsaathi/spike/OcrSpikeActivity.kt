package dev.techietitan.raahsaathi.spike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.techietitan.raahsaathi.ui.theme.RaahsaathiTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Developer-only screen for the step 1.4 OCR spike. Not part of the product UI. */
class OcrSpikeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val spike = OcrSpike(applicationContext)
        setContent {
            RaahsaathiTheme {
                var running by remember { mutableStateOf(false) }
                var text by remember {
                    mutableStateOf("Sample folder:\n${spike.sampleDir.absolutePath}\n\nPress Run when the sample is pushed.")
                }
                val scope = rememberCoroutineScope()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                ) { inner ->
                    Column(
                        modifier = Modifier.padding(inner).padding(16.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text("OCR spike (ML Kit, BSTD sample)", style = MaterialTheme.typography.headlineLarge)
                        Button(
                            onClick = {
                                running = true
                                scope.launch {
                                    text = try {
                                        withContext(Dispatchers.Default) {
                                            spike.run { done, total ->
                                                if (done % 10 == 0 || done == total) text = "Processed $done / $total"
                                            }
                                        }
                                    } catch (e: Exception) {
                                        "Failed: ${e.message}"
                                    }
                                    running = false
                                }
                            },
                            enabled = !running,
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                        ) { Text(if (running) "Running..." else "Run", style = MaterialTheme.typography.titleLarge) }
                        Text(text, style = MaterialTheme.typography.bodyLarge, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}
