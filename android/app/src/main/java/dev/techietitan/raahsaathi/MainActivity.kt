package dev.techietitan.raahsaathi

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.techietitan.raahsaathi.spike.OcrSpikeActivity
import dev.techietitan.raahsaathi.ui.theme.AppScheme
import dev.techietitan.raahsaathi.ui.theme.RaahsaathiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var scheme by remember { mutableStateOf(AppScheme.Daylight) }
            RaahsaathiTheme(scheme = scheme) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                ) { innerPadding ->
                    ThemeCheck(
                        scheme = scheme,
                        onSchemeChange = { scheme = it },
                        onOpenSpike = { startActivity(Intent(this, OcrSpikeActivity::class.java)) },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

/** Temporary screen to eyeball the theme in step 1.2. Replaced by the real home screen later. */
@Composable
fun ThemeCheck(
    scheme: AppScheme,
    onSchemeChange: (AppScheme) -> Unit,
    modifier: Modifier = Modifier,
    onOpenSpike: () -> Unit = {},
) {
    val extras = RaahsaathiTheme.extras
    val outline = BorderStroke(extras.outlineWidth, MaterialTheme.colorScheme.outline)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Raahsaathi", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onSurface)
        Text("राहसाथी", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onSurface)
        Text(
            "Scheme: ${scheme.name}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AppScheme.entries.forEach { option ->
            Button(
                onClick = { onSchemeChange(option) },
                modifier = Modifier.fillMaxWidth().height(72.dp),
                border = outline,
            ) { Text(option.name, style = MaterialTheme.typography.titleLarge) }
        }

        AlertSwatch("Critical: siren", extras.alerts.critical.container, extras.alerts.critical.content)
        AlertSwatch("High: close horn", extras.alerts.high.container, extras.alerts.high.content)
        AlertSwatch("Info: bell", extras.alerts.info.container, extras.alerts.info.content)

        Button(
            onClick = onOpenSpike,
            modifier = Modifier.fillMaxWidth().height(72.dp),
            border = outline,
        ) { Text("Dev: OCR spike", style = MaterialTheme.typography.titleLarge) }
    }
}

@Composable
private fun AlertSwatch(label: String, container: Color, content: Color) {
    val outlineColor = MaterialTheme.colorScheme.outline
    val width = RaahsaathiTheme.extras.outlineWidth
    Text(
        text = label,
        style = MaterialTheme.typography.titleLarge,
        color = content,
        modifier = Modifier
            .fillMaxWidth()
            .background(container, MaterialTheme.shapes.large)
            .border(width, outlineColor, MaterialTheme.shapes.large)
            .padding(16.dp),
    )
}

@Preview(showBackground = true)
@Composable
fun ThemeCheckPreview() {
    RaahsaathiTheme(scheme = AppScheme.Daylight) {
        ThemeCheck(scheme = AppScheme.Daylight, onSchemeChange = {})
    }
}
