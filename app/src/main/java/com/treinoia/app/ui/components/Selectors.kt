package com.treinoia.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

/**
 * Seletor de pílula única (estilo "segmented pills"), horizontal e rolável.
 * Usado no lugar de dropdowns tradicionais — mais rápido de tocar e mais
 * alinhado com o padrão visual de apps de treino atuais.
 */
@Composable
fun <T> PillSelectorRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    icon: ((T) -> ImageVector)? = null,
    onSelect: (T) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp)
    ) {
        items(options) { option ->
            val isSelected = option == selected
            Pill(
                text = label(option),
                icon = icon?.invoke(option),
                selected = isSelected,
                onClick = { onSelect(option) }
            )
        }
    }
}

/** Grade de pílulas (multi-linha) — usada para multi-seleção, como equipamentos. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiPillGrid(
    options: List<Pair<String, String>>, // valor -> label
    selectedValues: Set<String>,
    onToggle: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (valor, label) ->
            Pill(
                text = label,
                selected = valor in selectedValues,
                onClick = { onToggle(valor) }
            )
        }
    }
}

@Composable
private fun Pill(
    text: String,
    selected: Boolean,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .then(
                if (border != null) Modifier.border(border, RoundedCornerShape(50)) else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (icon != null) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = fg
        )
    }
}

/** Card de seção com título — organiza o formulário em blocos, como apps fitness fazem. */
@Composable
fun FormSection(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        content()
    }
}
