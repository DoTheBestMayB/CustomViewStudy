package com.dothebestmayb.customview.presentation.ui.paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.databinding.FragmentPaintBinding
import com.dothebestmayb.customview.presentation.ui.paint.adapter.PaintAdapter
import com.dothebestmayb.customview.presentation.ui.paint.adapter.VotingAdapter
import com.dothebestmayb.customview.presentation.ui.paint.model.AlertMessageType
import com.dothebestmayb.customview.presentation.ui.paint.model.GameType


class PaintFragment : Fragment() {

    private var _binding: FragmentPaintBinding? = null
    private val binding: FragmentPaintBinding
        get() = _binding!!

    private val viewModel: PaintViewModel by viewModels()
    private lateinit var paintAdapter: PaintAdapter
    private val votingAdapter = VotingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setGameMode()
    }

    private fun setGameMode() {
        val gameMode = GameType.MULTI
        viewModel.setGameType(gameMode)
        paintAdapter = PaintAdapter(
            gameMode,
            {
                viewModel.onClick(it)
            },
            {
                viewModel.addVoteItem(it)
            }
        )
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

        setRecyclerView()
        setListener()
        setObserve()
    }

    private fun setRecyclerView() {
        binding.rvItemBoard.adapter = paintAdapter
        binding.voteView.rvVoteStatus.adapter = votingAdapter
    }

    private fun setListener() {
        fun bottom() {
            binding.btnAddRect.setOnClickListener {
                viewModel.createRect()
            }
        }
        fun side() {
            binding.btnBackgroundColor.setOnClickListener {
                viewModel.changeSelectShapeColor()
            }
            binding.sliderTransparent.addOnChangeListener { slider, value, fromUser ->
                if (fromUser.not()) {
                    return@addOnChangeListener
                }
                viewModel.changeSelectShapeTransparent(value)
            }
        }
        fun paper() {
            binding.drawingPaper.doOnLayout {
                viewModel.setCanvasSize(binding.drawingPaper.width, binding.drawingPaper.height)
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

        bottom()
        side()
        paper()
    }

    private fun setObserve() {
        viewModel.tempRect.observe(viewLifecycleOwner) {
            binding.drawingPaper.drawTemp(it)
        }
        viewModel.drawingInfo.observe(viewLifecycleOwner) {
            binding.drawingPaper.submitShapeInfo(it)
            paintAdapter.submitList(it)
        }
        viewModel.selectedDrawingInfo.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.btnBackgroundColor.isEnabled = false
                binding.sliderTransparent.isEnabled = false
                binding.btnBackgroundColor.text = null
                binding.sliderTransparent.value = 5f
                return@observe
            }
            binding.btnBackgroundColor.setTextColor(it.shape.color.colorValue)
            binding.btnBackgroundColor.text = it.shape.color.toString()
            binding.sliderTransparent.value = it.shape.transparent.indicatorValue.toFloat()
            binding.btnBackgroundColor.isEnabled = true
            binding.sliderTransparent.isEnabled = true
        }
        viewModel.alertMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val message = when (it) {
                    AlertMessageType.VOTING_IS_UNDERWAY -> getString(R.string.vote_is_already_underway)
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.currentVotingItem.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.voteView.ivItem.setImageDrawable(null)
                // RecyclerView 아이템 null처리
                binding.voteView.btnAccept.isEnabled = false
                binding.voteView.tvName.text = null
                binding.voteView.btnDecline.isEnabled = false
                return@observe
            }
            binding.voteView.ivItem.setImageResource(R.drawable.sample_board_item) // TODO : 실제 이미지로 변경
            binding.voteView.tvName.text = it.shape.name
            binding.voteView.btnAccept.isEnabled = true
            binding.voteView.btnDecline.isEnabled = true
        }
        viewModel.remainVotingTime.observe(viewLifecycleOwner) {
            binding.voteView.progressRemainingTime.setProgress(it, true)
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}