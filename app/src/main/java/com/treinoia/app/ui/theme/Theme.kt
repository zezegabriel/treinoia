package com.treinoia.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// TreinoIA é sempre escuro (identidade visual de "academia"), independente do tema do sistema.
private val TreinoDarkScheme = darkColorScheme(
    primary = Iron,
    onPrimary = Concrete,
    secondary = IronDim,
    background = Concrete,
    onBackground = Chalk,
    surface = ConcreteRaised,
    onSurface = Chalk,
    surfaceVariant = Steel,
    onSurfaceVariant = ChalkDim,
    outline = Plate,
    error = ErrorRed,
)

@Composable
fun TreinoIATheme(content: @Composable () -> Unit) {
    val colorScheme = TreinoDarkScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TreinoTypography,
        content = content
    )
}
