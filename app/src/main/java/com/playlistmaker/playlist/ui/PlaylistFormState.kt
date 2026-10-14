package com.playlistmaker.playlist.ui

import android.net.Uri

/**
 * Данные формы создания/редактирования плейлиста.
 *
 * @param coverUri новая обложка, выбранная пользователем.
 * @param currentCoverPath уже сохранённая обложка редактируемого плейлиста.
 */
data class PlaylistFormState(
    val name: String = "",
    val description: String = "",
    val coverUri: Uri? = null,
    val currentCoverPath: String = "",
) {
    val isSaveButtonEnabled: Boolean
        get() = name.isNotBlank()
}
