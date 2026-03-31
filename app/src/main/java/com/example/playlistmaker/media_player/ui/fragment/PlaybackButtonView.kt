package com.example.playlistmaker.media_player.ui.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R

class PlaybackButtonView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    var playbackClickListener: () -> Unit  = {}

    private val imagePause: Bitmap?
    private val imagePlay: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var isPlaying = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                imagePause = getDrawable(R.styleable.PlaybackButtonView_imagePauseResId)?.toBitmap()
                imagePlay = getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)?.toBitmap()

            } finally {
                recycle()
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (!isPlaying){
            imagePlay?.let {
                canvas.drawBitmap(it, null, imageRect, null)
            }
        } else {
            imagePause?.let {
                canvas.drawBitmap(it, null, imageRect, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                return true
            MotionEvent.ACTION_UP -> {
                buttonState(!isPlaying)
                playbackClickListener.invoke()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    fun buttonState(isPlayingState: Boolean){
        isPlaying = isPlayingState
        invalidate()
    }
}