package com.dothebestmayb.customview.presentation.ui.paint

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dothebestmayb.customview.databinding.FragmentPaintBinding


class PaintFragment : Fragment() {

    private var _binding: FragmentPaintBinding? = null
    private val binding: FragmentPaintBinding
        get() = _binding!!

    private val viewModel: PaintViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaintBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
        setObserve()
    }

    private fun setListener() {
        binding.drawingPaper.doOnLayout {
            viewModel.setCanvasSize(binding.drawingPaper.width, binding.drawingPaper.height)
        }
        binding.btnAddRect.setOnClickListener {
            viewModel.createRect()
        }
        binding.drawingPaper.setOnTouchListener { v, event ->
            if (event == null) {
                return@setOnTouchListener false
            }
            val pointX = event.x
            val pointY = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.updateTouchStartPoint(pointX, pointY)
                }

                MotionEvent.ACTION_MOVE -> {
                    viewModel.updateTouchMove(pointX, pointY)
                }

                MotionEvent.ACTION_UP -> {
                    binding.drawingPaper.performClick()
                    viewModel.updateTouchEndPoint(pointX, pointY)
                }

                else -> {
                    return@setOnTouchListener false
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun setObserve() {
        viewModel.tempRect.observe(viewLifecycleOwner) {
            binding.drawingPaper.drawTemp(it)
        }
        viewModel.drawingInfo.observe(viewLifecycleOwner) {
            binding.drawingPaper.submitShapeInfo(it)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}