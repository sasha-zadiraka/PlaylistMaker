package com.playlistmaker.medialibrary.ui.playlists

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class PlaylistGridSpacingItemDecoration :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position == RecyclerView.NO_POSITION) {
            return
        }

        val horizontalHalfSpacing =
            view.dpToPx(HORIZONTAL_SPACING_DP) / 2

        val verticalSpacing =
            view.dpToPx(VERTICAL_SPACING_DP)

        val column = position % SPAN_COUNT

        outRect.left = if (column == LEFT_COLUMN) {
            0
        } else {
            horizontalHalfSpacing
        }

        outRect.right = if (column == LEFT_COLUMN) {
            horizontalHalfSpacing
        } else {
            0
        }

        outRect.top = 0
        outRect.bottom = verticalSpacing
    }

    private fun View.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
    }

    companion object {
        private const val SPAN_COUNT = 2
        private const val LEFT_COLUMN = 0

        private const val HORIZONTAL_SPACING_DP = 8
        private const val VERTICAL_SPACING_DP = 16
    }
}