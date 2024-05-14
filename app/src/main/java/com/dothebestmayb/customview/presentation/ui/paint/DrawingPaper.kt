package com.dothebestmayb.customview.presentation.ui.paint

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.presentation.model.DrawingInfo
import com.dothebestmayb.customview.presentation.model.DrawingType
import com.dothebestmayb.customview.presentation.model.Transparent

class DrawingPaper(context: Context, attrs: AttributeSet) : View(context, attrs) {


    @ColorInt
    private var color: Int
    private var type: DrawingType
    private var alpha: Transparent = Transparent.TEN

    private val paint: Paint
    private var drawingInfo: List<DrawingInfo> = listOf()
    private var tempRect: Rect? = null
    private val rectForSelecting = Rect()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DrawingPaper,
            0, 0
        ).apply {
            try {
                color = getInt(R.styleable.DrawingPaper_drawingColor, Color.parseColor(BLACK))
                type = DrawingType.from(getInteger(R.styleable.DrawingPaper_shape, 0))
            } finally {
                recycle()
            }

            paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = this.color
                style = Paint.Style.FILL
                strokeWidth = 8f
            }
        }
    }

    fun drawTemp(rect: Rect?) {
        tempRect = rect
        if (rect != null) {
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            val tRect = tempRect
            if (tRect != null) {
                paint.color = color
                paint.alpha = alpha.value
                paint.style = Paint.Style.FILL
                drawRect(tRect, paint)
            }

            for (info in drawingInfo) {
                when (info) {
                    is DrawingInfo.DrawingRectInfo -> {
                        paint.color = info.shape.color.colorValue
                        paint.alpha = info.shape.transparent.value
                        paint.style = Paint.Style.FILL
                        drawRect(info.rect, paint)

                        if (info.shape.clicked) {
                            paint.style = Paint.Style.STROKE
                            paint.color = info.shape.color.complementaryColor
                            paint.alpha = Transparent.TEN.value
                            rectForSelecting.set(
                                info.shape.point.x - 1,
                                info.shape.point.y - 1,
                                info.shape.point.x + info.shape.size.width + 1,
                                info.shape.point.y + info.shape.size.height + 1,
                            )
                            drawRect(rectForSelecting, paint)
                        }
                    }
                }
            }
            paint.color = color
        }
    }

    fun submitShapeInfo(drawingInfo: List<DrawingInfo>) {
        this.drawingInfo = drawingInfo
        invalidate()
    }

    override fun performClick(): Boolean {
        super.performClick()
        announceForAccessibility(context.getString(R.string.talkback_draw_shape_is_done))
        return true
    }

    companion object {
        private const val BLACK = "#FFFFFF"
    }
}