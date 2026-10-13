package com.playlistmaker.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme

/**
 * Диалог подтверждения с двумя кнопками.
 * Показывается, пока [visible] == true; скрытие — забота вызывающего кода.
 */
@Composable
fun ConfirmDialog(
    visible: Boolean,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String? = null,
) {
    if (!visible) return

    val colors = PlaylistMakerTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.bottomSheet,
        title = title?.let {
            {
                Text(
                    text = it,
                    style = AppTypography.placeholder,
                    color = colors.textPrimary,
                )
            }
        },
        text = {
            Text(
                text = message,
                style = AppTypography.primary,
                color = colors.textPrimary,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissText,
                    style = AppTypography.button,
                    color = colors.accent,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    style = AppTypography.button,
                    color = colors.accent,
                )
            }
        },
    )
}
