package com.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var isPlaying = false

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private val playIconResId: Int
    private val pauseIconResId: Int

    private val imageRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            0
        )

        playIconResId = typedArray.getResourceId(
            R.styleable.PlaybackButtonView_playIcon, 0
        )

        pauseIconResId = typedArray.getResourceId(
            R.styleable.PlaybackButtonView_pauseIcon, 0
        )

        typedArray.recycle()

        isClickable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        playBitmap = createBitmap(playIconResId)
        pauseBitmap = createBitmap(pauseIconResId)

        val bitmapWidth = playBitmap?.width ?: pauseBitmap?.width ?: 0
        val bitmapHeight = playBitmap?.height ?: pauseBitmap?.height ?: 0

        val left = (w - bitmapWidth) / 2f
        val top = (h - bitmapHeight) / 2f

        imageRect.set(
            left,
            top,
            left + bitmapWidth,
            top + bitmapHeight
        )
    }

    private fun createBitmap(resId: Int): Bitmap? {
        if (resId == 0) return null

        val drawable = ContextCompat.getDrawable(context, resId) ?: return null
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        if (width <= 0 || height <= 0) return null

        return drawable.toBitmap(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bitmap = if (isPlaying) pauseBitmap else playBitmap
        bitmap?.let { canvas.drawBitmap(it, null, imageRect, paint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }

            MotionEvent.ACTION_UP -> {
                setPlaying(!isPlaying)
                performClick()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    fun setPlaying(playing: Boolean) {
        if (isPlaying == playing) return

        isPlaying = playing
        invalidate()
    }
}
