package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = TrafficBlue,
    onPrimary = Color.White,
    primaryContainer = TrafficSurfaceElevated,
    onPrimaryContainer = TrafficBlueLight,
    secondary = TrafficCyan,
    onSecondary = Color.Black,
    secondaryContainer = TrafficSurfaceVariant,
    onSecondaryContainer = Color.White,
    tertiary = TrafficYellow,
    onTertiary = Color.Black,
    background = TrafficDarkBg,
    onBackground = TextPrimary,
    surface = TrafficSurface,
    onSurface = TextPrimary,
    surfaceVariant = TrafficSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TrafficBorder,
    error = TrafficRed,
    onError = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TrafficBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE6F4FF),
    onPrimaryContainer = TrafficBlueHover,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    tertiary = Color(0xFFD97706),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = TrafficRed,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek CCTV surveillance dark theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
