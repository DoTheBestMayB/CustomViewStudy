package com.dothebestmayb.customview.presentation.ui.dashedline

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ComposeShader
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingColor
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingInfo
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingShape
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingType
import com.dothebestmayb.customview.presentation.ui.paint.model.Point
import com.dothebestmayb.customview.presentation.ui.paint.model.Size
import com.dothebestmayb.customview.presentation.ui.paint.model.TouchState
import com.dothebestmayb.customview.presentation.ui.paint.model.Transparent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashedLinePaper(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }
    private val path = Path()

    //    private val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sample_placeholder)
    private lateinit var bitmap: Bitmap

    private var xLocation = 0f
    private var yLocation = 0f

    fun calculate(bitmap: Bitmap) {
        this.bitmap = bitmap

        CoroutineScope(Dispatchers.IO).launch {
            val divideNum = 30
            val resetNum = -width / 3f + 10
            xLocation = resetNum
            while (true) {
                xLocation += 10
                if (xLocation >= divideNum) {
                    xLocation = resetNum
                }
                invalidate()
                delay(50)
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, xLocation, yLocation, paint)
        canvas.restore()
    }

    override fun performClick(): Boolean {
        super.performClick()
        announceForAccessibility(context.getString(R.string.talkback_draw_shape_is_done))
        return true
    }

    fun updateTouchStartPoint(pointX: Float, pointY: Float) {
        path.reset()

        path.moveTo(pointX, pointY)
        path.lineTo(pointX, pointY)
    }

    fun updateTouchMove(pointX: Float, pointY: Float) {
        path.lineTo(pointX, pointY)
//        path.moveTo(pointX, pointY)
    }

    fun updateTouchEndPoint(pointX: Float, pointY: Float) {
        path.lineTo(pointX, pointY)
        path.close()
    }
}