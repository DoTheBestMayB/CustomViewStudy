package com.dothebestmayb.customview.presentation.ui.paint

import android.graphics.Rect
import androidx.annotation.FloatRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dothebestmayb.customview.presentation.model.AlertMessageType
import com.dothebestmayb.customview.presentation.model.DrawingColor
import com.dothebestmayb.customview.presentation.model.DrawingInfo
import com.dothebestmayb.customview.presentation.model.DrawingShape
import com.dothebestmayb.customview.presentation.model.DrawingType
import com.dothebestmayb.customview.presentation.model.Event
import com.dothebestmayb.customview.presentation.model.Point
import com.dothebestmayb.customview.presentation.model.Size
import com.dothebestmayb.customview.presentation.model.Transparent
import com.dothebestmayb.customview.presentation.ui.paint.model.GameType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class PaintViewModel : ViewModel() {


    private val random = Random(System.currentTimeMillis())

    // 아래 정보를 Data class로 모델링해서 가지고 있기
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

    private val _selectedDrawingInfo = MutableLiveData<DrawingInfo?>()
    val selectedDrawingInfo: LiveData<DrawingInfo?>
        get() = _selectedDrawingInfo

    private val _currentVotingItem = MutableLiveData<DrawingInfo?>()
    val currentVotingItem: LiveData<DrawingInfo?>
        get() = _currentVotingItem

    private val _alertMessage = MutableLiveData<Event<AlertMessageType>>()
    val alertMessage: LiveData<Event<AlertMessageType>>
        get() = _alertMessage

    private val _remainVotingTime = MutableLiveData<Int>()
    val remainVotingTime: LiveData<Int>
        get() = _remainVotingTime

    private var gameType: GameType = GameType.SINGLE

    fun setGameType(type: GameType) {
        gameType = type
    }

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
        _tempRect.value = null
        if (abs(lastX - firstX) < 10 && abs(lastY - firstY) < 10) {
            checkExistingShape(lastX, lastY)
        } else {
            createNewShape()
        }
    }

    private fun checkExistingShape(x: Float, y: Float) {
        val information = _drawingInfo.value?.toMutableList() ?: return
        var isFind = false
        for (idx in information.lastIndex downTo 0) {
            when (val drawingInfo = information[idx]) {
                is DrawingInfo.DrawingRectInfo -> {
                    if (isFind) {
                        val new = drawingInfo.copy(shape = drawingInfo.shape.copy(clicked = false))
                        information[idx] = new
                        continue
                    }
                    val clicked = drawingInfo.shape.isClicked(x.toInt(), y.toInt())
                    val new = drawingInfo.copy(shape = drawingInfo.shape.copy(clicked = clicked))
                    information[idx] = new
                    if (clicked) {
                        _selectedDrawingInfo.value = new
                        isFind = true
                    }
                }
            }
        }
        if (isFind.not()) {
            _selectedDrawingInfo.value = null
        }
        _drawingInfo.value = information
    }

    private fun createNewShape() {
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

            DrawingType.UNKNOWN -> TODO()
        }
        information.add(info)
        _drawingInfo.value = information
    }

    fun setCanvasSize(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    fun changeSelectShapeColor() {
        val information = _drawingInfo.value?.toMutableList() ?: return
        val selectedDrawing = _selectedDrawingInfo.value ?: return

        val idx = information.indexOfFirst {
            it == selectedDrawing
        }
        val newDrawing = when (selectedDrawing) {
            is DrawingInfo.DrawingRectInfo -> selectedDrawing.copy(
                shape = selectedDrawing.shape.copy(
                    color = DrawingColor.random(random)
                )
            )
        }
        information[idx] = newDrawing
        _drawingInfo.value = information
        _selectedDrawingInfo.value = newDrawing
    }

    fun changeSelectShapeTransparent(@FloatRange(1.0, 10.0) value: Float) {
        val information = _drawingInfo.value?.toMutableList() ?: return
        val selectedDrawing = _selectedDrawingInfo.value ?: return

        val idx = information.indexOfFirst {
            it == selectedDrawing
        }
        val newDrawing = when (selectedDrawing) {
            is DrawingInfo.DrawingRectInfo -> selectedDrawing.copy(
                shape = selectedDrawing.shape.copy(
                    transparent = Transparent.from(value.toInt())
                )
            )
        }
        information[idx] = newDrawing
        _drawingInfo.value = information
        _selectedDrawingInfo.value = newDrawing
    }

    fun onClick(drawingInfo: DrawingInfo) {
        val information = _drawingInfo.value?.toMutableList() ?: return

        for (idx in information.indices) {
            val item = information[idx]
            val clicked = item == drawingInfo

            val newItem = when (item) {
                is DrawingInfo.DrawingRectInfo -> item.copy(
                    shape = item.shape.copy(
                        clicked = clicked
                    )
                )
            }
            information[idx] = newItem
            if (clicked) {
                _selectedDrawingInfo.value = newItem
            }
        }
        _drawingInfo.value = information
    }

    fun addVoteItem(drawingInfo: DrawingInfo) {
        if (_currentVotingItem.value != null) {
            _alertMessage.value = Event(AlertMessageType.VOTING_IS_UNDERWAY)
            return
        }
        _currentVotingItem.value = drawingInfo
        viewModelScope.launch {
            var count = VOTING_TOTAL_TIME
            while (count > 0) {
                delay(100)
                count -= 100
                _remainVotingTime.value = 100 * count / VOTING_TOTAL_TIME
            }
            _currentVotingItem.value = null
        }
    }
    companion object {
        private const val VOTING_TOTAL_TIME = 20_000
    }
}