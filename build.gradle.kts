// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Desde a AGP 9.0, o suporte a Kotlin é embutido — não é mais necessário (nem permitido)
// aplicar o plugin org.jetbrains.kotlin.android separadamente.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
