package dev.techietitan.raahsaathi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** The three schemes in docs/09. Night follows the system unless the user overrides it. */
enum class AppScheme { Daylight, Night, MaxContrast }

/** Fill and text colour for one alert tier. */
data class AlertColor(val container: Color, val content: Color)

data class AlertColors(
    val critical: AlertColor,
    val high: AlertColor,
    val info: AlertColor,
)

/** Non-Material tokens: alert tiers, focus ring, outline width. */
data class RaahsaathiExtras(
    val alerts: AlertColors,
    val focusRing: Color,
    val outlineWidth: Dp,
)

private val DaylightColors = lightColorScheme(
    primary = DaylightPrimary,
    onPrimary = DaylightOnPrimary,
    secondary = DaylightSecondary,
    onSecondary = DaylightOnSecondary,
    background = DaylightSurface,
    onBackground = DaylightOnSurface,
    surface = DaylightSurface,
    onSurface = DaylightOnSurface,
    surfaceContainer = DaylightSurfaceContainer,
    onSurfaceVariant = DaylightOnSurfaceVariant,
    outline = DaylightOutline,
    error = DaylightError,
    onError = DaylightOnError,
)

private val NightColors = darkColorScheme(
    primary = NightPrimary,
    onPrimary = NightOnPrimary,
    secondary = NightSecondary,
    onSecondary = NightOnSecondary,
    background = NightSurface,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
    surfaceContainer = NightSurfaceContainer,
    onSurfaceVariant = NightOnSurfaceVariant,
    outline = NightOutline,
    error = NightError,
    onError = NightOnError,
)

private val MaxContrastColors = darkColorScheme(
    primary = MaxPrimary,
    onPrimary = MaxOnPrimary,
    secondary = MaxSecondary,
    onSecondary = MaxOnSecondary,
    background = MaxSurface,
    onBackground = MaxOnSurface,
    surface = MaxSurface,
    onSurface = MaxOnSurface,
    surfaceContainer = MaxSurface,
    onSurfaceVariant = MaxOnSurface,
    outline = MaxOutline,
    error = MaxError,
    onError = MaxOnError,
)

private val DaylightAlerts = AlertColors(
    critical = AlertColor(AlertCriticalDaylight, OnAlertCriticalDaylight),
    high = AlertColor(AlertHigh, OnAlertHigh),
    info = AlertColor(AlertInfoDaylight, OnAlertInfoDaylight),
)

// docs/09 gives no separate Max-contrast alert palette, so it reuses Night's.
private val NightAlerts = AlertColors(
    critical = AlertColor(AlertCriticalNight, OnAlertCriticalNight),
    high = AlertColor(AlertHigh, OnAlertHigh),
    info = AlertColor(AlertInfoNight, OnAlertInfoNight),
)

private val RaahsaathiShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

private val LocalExtras = staticCompositionLocalOf<RaahsaathiExtras> {
    error("RaahsaathiTheme not provided")
}

object RaahsaathiTheme {
    val extras: RaahsaathiExtras
        @Composable get() = LocalExtras.current
}

private fun colorSchemeFor(scheme: AppScheme): ColorScheme = when (scheme) {
    AppScheme.Daylight -> DaylightColors
    AppScheme.Night -> NightColors
    AppScheme.MaxContrast -> MaxContrastColors
}

private fun extrasFor(scheme: AppScheme) = when (scheme) {
    AppScheme.Daylight -> RaahsaathiExtras(DaylightAlerts, FocusDaylight, 2.dp)
    AppScheme.Night -> RaahsaathiExtras(NightAlerts, FocusNightAndMax, 2.dp)
    AppScheme.MaxContrast -> RaahsaathiExtras(NightAlerts, FocusNightAndMax, 3.dp)
}

/**
 * Dynamic colour is deliberately off: the contrast ratios in docs/09 only hold for these fixed tokens.
 */
@Composable
fun RaahsaathiTheme(
    scheme: AppScheme = if (isSystemInDarkTheme()) AppScheme.Night else AppScheme.Daylight,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalExtras provides extrasFor(scheme)) {
        MaterialTheme(
            colorScheme = colorSchemeFor(scheme),
            typography = Typography,
            shapes = RaahsaathiShapes,
            content = content,
        )
    }
}
