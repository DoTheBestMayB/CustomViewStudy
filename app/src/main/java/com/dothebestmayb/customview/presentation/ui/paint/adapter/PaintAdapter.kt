package com.dothebestmayb.customview.presentation.ui.paint.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dothebestmayb.customview.R
import com.dothebestmayb.customview.databinding.ItemForBoardBinding
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingInfo
import com.dothebestmayb.customview.presentation.ui.paint.model.DrawingType
import com.dothebestmayb.customview.presentation.ui.paint.model.GameType

class PaintAdapter(
    private val gameType: GameType,
    private val onItemClick: (DrawingInfo) -> Unit,
    private val onVoteClick: (DrawingInfo) -> Unit,
) : ListAdapter<DrawingInfo, ViewHolder>(diff) {

    class RectViewHolder(
        private val binding: ItemForBoardBinding,
        gameType: GameType,
        private val onItemClick: (DrawingInfo) -> Unit,
        private val onVoteClick: (DrawingInfo) -> Unit,
    ) : ViewHolder(binding.root) {

        private lateinit var drawingInfo: DrawingInfo

        init {
            binding.root.setOnClickListener {
                onItemClick(drawingInfo)
            }

            val res = when(gameType) {
                GameType.SINGLE -> R.drawable.baseline_close_24
                GameType.MULTI -> R.drawable.baseline_how_to_vote_24
            }
            binding.btnVote.setImageResource(res)

        }

        fun bind(drawingInfo: DrawingInfo.DrawingRectInfo, order: Int) {
            this.drawingInfo = drawingInfo

            val orderText = (order + 1).toString()
            binding.tvOrder.text = orderText
            binding.tvName.text = drawingInfo.shape.name

            binding.btnVote.setOnClickListener {
                onVoteClick(drawingInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val type = DrawingType.from(viewType)
        return when (type) {
            DrawingType.UNKNOWN -> TODO()
            DrawingType.LINE -> TODO()
            DrawingType.RECT -> RectViewHolder(
                ItemForBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                gameType,
                onItemClick,
                onVoteClick,
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is RectViewHolder -> holder.bind(
                getItem(position) as DrawingInfo.DrawingRectInfo,
                position
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is DrawingInfo.DrawingRectInfo -> DrawingType.RECT.typeNum
        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<DrawingInfo>() {
            override fun areItemsTheSame(oldItem: DrawingInfo, newItem: DrawingInfo): Boolean {
                return if (oldItem is DrawingInfo.DrawingRectInfo && newItem is DrawingInfo.DrawingRectInfo) {
                    oldItem.shape.id == newItem.shape.id
                } else {
                    false
                }
            }

            override fun areContentsTheSame(oldItem: DrawingInfo, newItem: DrawingInfo): Boolean {
                return oldItem == newItem
            }

        }
    }
}