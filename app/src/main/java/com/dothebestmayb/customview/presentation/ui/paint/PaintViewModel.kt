package com.dothebestmayb.customview.presentation.ui.paint

import android.graphics.Rect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dothebestmayb.customview.presentation.model.DrawingColor
import com.dothebestmayb.customview.presentation.model.DrawingInfo
import com.dothebestmayb.customview.presentation.model.DrawingShape
import com.dothebestmayb.customview.presentation.model.DrawingType
import com.dothebestmayb.customview.presentation.model.Point
import com.dothebestmayb.customview.presentation.model.Size
import com.dothebestmayb.customview.presentation.model.Transparent
import kotlin.random.Random

class PaintViewModel : ViewModel() {


    private val random = Random(System.currentTimeMillis())

    private var width = -1
    private var height = -1
    private var firstX = -1f
    private var firstY = -1f
    private var lastX = -1f
    private var lastY = -1f

    private var color = DrawingColor(0u, 0u, 0u)
    private var transParent = Transparent.TEN
    private var drawingType = DrawingType.RECT

    private val _tempRect = MutableLiveData<Rect?>()
    val tempRect: LiveData<Rect?>
        get() = _tempRect

    private val _drawingInfo = MutableLiveData<List<DrawingInfo>>()
    val drawingInfo: LiveData<List<DrawingInfo>>
        get() = _drawingInfo

    fun createRect() {
        val size = Size(Random.nextInt(width), Random.nextInt(height))
        val point = Point(
            Random.nextInt(width - size.width),
            Random.nextInt(height - size.height),
        )
        val color = DrawingColor(
            r = Random.nextInt(256).toUByte(),
            g = Random.nextInt(256).toUByte(),
            b = Random.nextInt(256).toUByte()
        )
        val transparent = Transparent.entries.random(random)

        val drawingShape = DrawingShape(
            size = size,
            point = point,
            color = color,
            transparent = transparent,
            type = DrawingType.RECT,
        )
        val info = _drawingInfo.value?.toMutableList() ?: mutableListOf()
        info.add(
            DrawingInfo.DrawingRectInfo(
                drawingShape,
                Rect(point.x, point.y, point.x + size.width, point.y + size.height)
            )
        )
        _drawingInfo.value = info
    }

    fun updateTouchStartPoint(pointX: Float, pointY: Float) {
        firstX = pointX
        firstY = pointY
    }

    fun updateTouchMove(pointX: Float, pointY: Float) {
        lastX = pointX
        lastY = pointY
        _tempRect.value = Rect(firstX.toInt(), firstY.toInt(), lastX.toInt(), lastY.toInt())
    }

    fun updateTouchEndPoint(pointX: Float, pointY: Float) {
        lastX = pointX
        lastY = pointY
        val shape = DrawingShape(
            size = Size(
                width = lastX.toInt() - firstX.toInt(),
                height = lastY.toInt() - firstY.toInt()
            ),
            point = Point(x = firstX.toInt(), y = firstY.toInt()),
            color = color,
            transparent = transParent,
            type = DrawingType.RECT,
        )
        val information = _drawingInfo.value?.toMutableList() ?: mutableListOf()
        val info = when (drawingType) {
            DrawingType.LINE -> TODO()
            DrawingType.RECT -> DrawingInfo.DrawingRectInfo(
                shape,
                Rect(
                    shape.point.x,
                    shape.point.y,
                    shape.point.x + shape.size.width,
                    shape.point.y + shape.size.height
                )
            )
        }
        information.add(info)
        _tempRect.value = null
        _drawingInfo.value = information
    }

    fun setCanvasSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }
}