package com.dothebestmayb.customview.presentation.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.presentation.model.DrawingPaperType

class DrawingPaper(context: Context, attrs: AttributeSet) : View(context, attrs) {


    @ColorInt
    private var color: Int
    private var type: DrawingPaperType

    private val paint: Paint
    private val rects: MutableList<Rect> = mutableListOf()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DrawingPaper,
            0, 0
        ).apply {
            try {
                color = getInt(R.styleable.DrawingPaper_drawingColor, Color.parseColor(BLACK))
                type = DrawingPaperType.from(getInteger(R.styleable.DrawingPaper_shape, 0))
            } finally {
                recycle()
            }

            paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = this.color
                style = Paint.Style.STROKE
            }
        }
    }

    fun getColor(): Int = color

    fun setColor(@ColorInt color: Int) {
        this.color = color
        invalidate()
        requestLayout()
    }

    fun getType(): DrawingPaperType = type

    fun setType(type: DrawingPaperType) {
        this.type = type
        invalidate()
        requestLayout()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)


    }

    fun createRandomRect() {
        val drawingShape = DrawingShape.createRandom
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            val tRect = tempRect
            if (tRect != null) {
                drawRect(tRect, paint)
            }
            for (rect in rects) {
                drawRect(rect, paint)
            }
        }
    }

    private var firstX = -1f
    private var firstY = -1f
    private var lastX = -1f
    private var lastY = -1f
    private var tempRect: Rect? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }
        val pointX = event.x
        val pointY = event.y

        if (event.action == MotionEvent.ACTION_DOWN) {
            firstX = pointX
            firstY = pointY
            return true
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            lastX = pointX
            lastY = pointY
            tempRect = Rect(firstX.toInt(), firstY.toInt(), lastX.toInt(), lastY.toInt())
            invalidate()
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            val rect = tempRect
            if (rect != null) {
                rects.add(rect)
            }
            invalidate()
            tempRect = null
            return true
        }
        return super.onTouchEvent(event)
    }

    companion object {
        private const val BLACK = "#FFFFFF"
    }
}