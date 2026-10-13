package com.playlistmaker.playlist.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.playlistmaker.ui.components.BackTopBar
import com.playlistmaker.ui.components.CoverImage
import com.playlistmaker.ui.components.PrimaryButton
import com.playlistmaker.ui.theme.AppTypography
import com.playlistmaker.ui.theme.PlaylistMakerTheme
import java.io.File

/**
 * Общая вёрстка экранов «Новый плейлист» и «Редактировать»:
 * обложка, поля «Название» и «Описание», кнопка сохранения внизу.
 */
@Composable
fun PlaylistEditorContent(
    title: String,
    saveButtonText: String,
    form: PlaylistFormState,
    isSaveButtonEnabled: Boolean,
    onBackClick: () -> Unit,
    onCoverClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlaylistMakerTheme.colors.background)
            .systemBarsPadding()
            .imePadding(),
    ) {
        BackTopBar(
            onBackClick = onBackClick,
            title = title,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            val cover = remember(form.coverUri, form.currentCoverPath) {
                form.coverUri ?: form.currentCoverPath.takeIf { it.isNotBlank() }?.let(::File)
            }

            PlaylistCoverPicker(
                cover = cover,
                onClick = onCoverClick,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            PlaylistTextField(
                value = form.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.new_playlist_name),
                singleLine = true,
                modifier = Modifier.padding(top = 32.dp),
            )

            PlaylistTextField(
                value = form.description,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.new_playlist_description),
                singleLine = false,
                modifier = Modifier.padding(top = 16.dp),
            )
        }

        PrimaryButton(
            text = saveButtonText,
            onClick = onSaveClick,
            enabled = isSaveButtonEnabled,
            modifier = Modifier.padding(start = 17.dp, end = 17.dp, bottom = 32.dp),
        )
    }
}

@Composable
private fun PlaylistCoverPicker(
    cover: Any?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = PlaylistMakerTheme.colors.inactive
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .clickable(onClick = onClick)
            .drawBehind {
                if (cover == null) {
                    val strokeWidth = 1.dp.toPx()
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(30.dp.toPx(), 30.dp.toPx())
                            ),
                        ),
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (cover == null) {
            Image(
                painter = painterResource(R.drawable.ic_add_background_80),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
            )
        } else {
            CoverImage(
                model = cover,
                placeholderRes = R.drawable.ic_add_background_80,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun PlaylistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = PlaylistMakerTheme.colors
    val activeColor = if (value.isNotEmpty()) colors.accent else colors.inactive

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label, style = AppTypography.primary) },
        textStyle = AppTypography.primary,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 4,
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = if (singleLine) ImeAction.Next else ImeAction.Default,
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = colors.accent,
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = activeColor,
            focusedLabelColor = colors.accent,
            unfocusedLabelColor = if (value.isNotEmpty()) colors.accent else colors.textPrimary,
        ),
    )
}
