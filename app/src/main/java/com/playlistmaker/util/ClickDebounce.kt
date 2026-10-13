package com.playlistmaker.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.playlistmaker.util.AppConstants.CLICK_DEBOUNCE_DELAY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Возвращает обработчик клика, который выполняет [action] не чаще,
 * чем раз в [delayMillis] — защита от двойного открытия экрана.
 */
@Composable
fun <T> rememberDebouncedClick(
    delayMillis: Long = CLICK_DEBOUNCE_DELAY,
    action: (T) -> Unit,
): (T) -> Unit {
    val scope = rememberCoroutineScope()
    val currentAction by rememberUpdatedState(action)

    return remember(scope, delayMillis) {
        var isClickAllowed = true

        { param: T ->
            if (isClickAllowed) {
                isClickAllowed = false
                currentAction(param)

                scope.launch {
                    delay(delayMillis)
                    isClickAllowed = true
                }
            }
        }
    }
}
