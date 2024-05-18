package com.dothebestmayb.customview.presentation.ui.paint

import android.graphics.Rect
import androidx.annotation.FloatRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dothebestmayb.customview.presentation.ui.paint.model.AlertMessageType
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingColor
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingInfo
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingShape
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingType
import com.dothebestmayb.customview.presentation.ui.paint.model.Event
import com.dothebestmayb.customview.presentation.ui.paint.model.Point
import com.dothebestmayb.customview.presentation.ui.paint.model.Size
import com.dothebestmayb.customview.presentation.ui.paint.model.Transparent
import com.dothebestmayb.customview.presentation.ui.paint.model.GameType
import com.dothebestmayb.customview.presentation.ui.paint.model.TouchState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class PaintViewModel : ViewModel() {


    private val random = Random(System.currentTimeMillis())

    // 아래 정보를 Data class로 모델링해서 가지고 있기
    private var canvasSize: Size = Size.Empty
    private var touchState = TouchState.Empty

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
        val size = Size(Random.nextInt(canvasSize.width), Random.nextInt(canvasSize.height))
        val point = Point(
            Random.nextInt(canvasSize.width - size.width),
            Random.nextInt(canvasSize.height - size.height),
        )
        val color = DrawingColor.random(random)
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
        touchState = TouchState(
            start = Point(x = pointX.toInt(), y = pointY.toInt()),
            end = Point.Empty,
        )
    }

    fun updateTouchMove(pointX: Float, pointY: Float) {
        touchState = touchState.copy(end = Point(x = pointX.toInt(), y = pointY.toInt()))

        _tempRect.value = Rect(
            touchState.start.x,
            touchState.start.y,
            touchState.end.x,
            touchState.end.y
        )
    }

    fun updateTouchEndPoint(pointX: Float, pointY: Float) {
        touchState = touchState.copy(end = Point(x = pointX.toInt(), y = pointY.toInt()))
        _tempRect.value = null
        if (checkClickGesture(touchState)) {
            handleClickGesture(pointX, pointY)
        } else {
            createNewShape()
        }
    }

    private fun checkClickGesture(touchState: TouchState): Boolean =
        abs(touchState.width) < 10 && abs(touchState.height) < 10

    private fun handleClickGesture(x: Float, y: Float) {
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
                width = touchState.width,
                height = touchState.height
            ),
            point = touchState.start,
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
        canvasSize = Size(width, height)
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