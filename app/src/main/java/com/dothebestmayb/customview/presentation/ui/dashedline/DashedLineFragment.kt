package com.dothebestmayb.customview.presentation.ui.dashedline

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnLayout
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.databinding.FragmentDashedLineBinding

class DashedLineFragment : Fragment() {

    private var _binding: FragmentDashedLineBinding? = null
    private val binding: FragmentDashedLineBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashedLineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.paper.doOnLayout {
            val bitmap = ContextCompat.getDrawable(requireContext(), R.drawable.sample_placeholder)!!.toBitmap(binding.paper.width * 2, binding.paper.height * 2, null)
            binding.paper.calculate(bitmap)
        }

        setListener()
    }

    private fun setListener() {
        binding.paper.setOnTouchListener { v, event ->
            if (event == null) {
                return@setOnTouchListener false
            }
            val pointX = event.x
            val pointY = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.paper.updateTouchStartPoint(pointX, pointY)
                }

                MotionEvent.ACTION_MOVE -> {
                    binding.paper.updateTouchMove(pointX, pointY)
                }

                MotionEvent.ACTION_UP -> {
                    binding.paper.performClick()
                    binding.paper.updateTouchEndPoint(pointX, pointY)

                }

                else -> {
                    return@setOnTouchListener false
                }
            }
            return@setOnTouchListener true
        }
    }

}