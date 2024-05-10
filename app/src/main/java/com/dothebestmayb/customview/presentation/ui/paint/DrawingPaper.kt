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

class DrawingPaper(context: Context, attrs: AttributeSet) : View(context, attrs) {


    @ColorInt
    private var color: Int
    private var type: DrawingType

    private val paint: Paint
    private var drawingInfo: List<DrawingInfo> = listOf()
    private var tempRect: Rect? = null

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
                style = Paint.Style.STROKE
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
                drawRect(tRect, paint)
            }

            for (info in drawingInfo) {
                when (info) {
                    is DrawingInfo.DrawingRectInfo -> {
                        paint.color = info.shape.color.colorValue
                        paint.alpha = info.shape.transparent.value
                        drawRect(info.rect, paint)
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

    companion object {
        private const val BLACK = "#FFFFFF"
    }
}