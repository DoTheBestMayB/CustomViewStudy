package com.dothebestmayb.customview.presentation.ui.paint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.databinding.FragmentPaintBinding
import com.dothebestmayb.customview.presentation.ui.paint.adapter.PaintAdapter
import com.dothebestmayb.customview.presentation.ui.paint.adapter.VotingAdapter
import com.dothebestmayb.customview.presentation.ui.paint.model.AlertMessageType
import com.dothebestmayb.customview.presentation.ui.paint.model.GameType
import com.dothebestmayb.customview.presentation.ui.paint.model.VotingInfo
import com.dothebestmayb.customview.presentation.ui.paint.model.VotingState


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
        // 임의로 값 지정, 실제로는 다른 Fragment로부터 값을 받아야 함
        val gameMode = GameType.MULTI
        val participantCount = 3

        viewModel.setGameMode(gameMode, participantCount)
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
        binding.voteView.rvVoteStatus.itemAnimator = null
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
            binding.edtName.doOnTextChanged { text, _, _, _ ->
                val isValueChangedByUser = binding.edtName.hasFocus()
                if (isValueChangedByUser.not()) {
                    return@doOnTextChanged
                }
                viewModel.updateName(text.toString())
            }
            binding.btnChangeName.setOnClickListener {
                viewModel.changeName()
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

        fun vote() {
            binding.voteView.btnAccept.setOnClickListener {
                viewModel.onVote(VotingState.ACCEPT)
            }
            binding.voteView.btnDecline.setOnClickListener {
                viewModel.onVote(VotingState.DECLINE)
            }
        }

        bottom()
        side()
        paper()
        vote()
    }

    private fun setObserve() {
        viewModel.inProgressDrawing.observe(viewLifecycleOwner) {
            binding.drawingPaper.drawTemp(it)
        }
        viewModel.drawingInfo.observe(viewLifecycleOwner) {
            binding.drawingPaper.submitShapeInfo(it)
            paintAdapter.submitList(it)
        }
        viewModel.selectedDrawingInfo.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            binding.btnBackgroundColor.setTextColor(it.shape.color.colorValue)
            binding.btnBackgroundColor.text = it.shape.color.toString()
            binding.sliderTransparent.value = it.shape.transparent.indicatorValue.toFloat()
            binding.btnBackgroundColor.isEnabled = true
            binding.sliderTransparent.isEnabled = true
            binding.edtName.setText(it.shape.name)
        }
        viewModel.alertMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val message = when (it) {
                    AlertMessageType.VOTING_IS_UNDERWAY -> getString(R.string.vote_is_already_underway)
                    AlertMessageType.CHANGING_VOTING_ITEM_IS_NOT_ALLOWED -> getString(R.string.changing_voting_item_is_not_allowed)
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.currentVotingItem.observe(viewLifecycleOwner) { votingInfo ->
            setVotingView(votingInfo)
        }
        viewModel.remainVotingTime.observe(viewLifecycleOwner) {
            binding.voteView.progressRemainingTime.setProgress(it, true)
        }
        viewModel.color.observe(viewLifecycleOwner) {
            binding.btnBackgroundColor.text = it.toString()
        }
    }

    private fun setVotingView(votingInfo: VotingInfo?) = with(binding.voteView) {
        if (votingInfo == null) {
            progressRemainingTime.progress = 0
            ivItem.setImageDrawable(null)
            tvName.text = null
            btnAccept.isEnabled = false
            btnDecline.isEnabled = false
            votingAdapter.submitList(null)
            return
        }
        ivItem.setImageResource(R.drawable.sample_board_item) // TODO : 실제 이미지로 변경
        tvName.text = votingInfo.drawingInfo.shape.name
        btnAccept.isEnabled = true
        btnDecline.isEnabled = true

        votingAdapter.submitList(votingInfo.votingStates)
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}