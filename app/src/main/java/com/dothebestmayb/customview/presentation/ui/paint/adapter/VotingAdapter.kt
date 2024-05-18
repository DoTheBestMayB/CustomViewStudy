package com.dothebestmayb.customview.presentation.ui.paint.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dothebestmayb.customview.databinding.ItemForVoteBinding
import com.dothebestmayb.customview.presentation.ui.paint.model.VotingState

class VotingAdapter : ListAdapter<VotingState, RecyclerView.ViewHolder>(diff) {

    class VotingViewHolder(private val binding: ItemForVoteBinding) : ViewHolder(binding.root) {

        fun bind(item: VotingState) {
            binding.tvResult.text = item.symbol
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as VotingViewHolder).bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VotingViewHolder(
            ItemForVoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    companion object {
        val diff = object : DiffUtil.ItemCallback<VotingState>() {
            override fun areItemsTheSame(oldItem: VotingState, newItem: VotingState): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VotingState, newItem: VotingState): Boolean {
                return oldItem == newItem
            }
        }
    }

}
