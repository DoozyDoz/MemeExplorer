package com.example.memeexplorer.common.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memeexplorer.common.presentation.model.UIMeme
import com.example.memeexplorer.common.utils.setImage
import com.example.memeexplorer.databinding.RecyclerViewMemeItemBinding


class MemesAdapter : ListAdapter<UIMeme, MemesAdapter.MemesViewHolder>(ITEM_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemesViewHolder {
        val binding = RecyclerViewMemeItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MemesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemesViewHolder, position: Int) {
        val item: UIMeme = getItem(position)

        holder.bind(item)
    }

    inner class MemesViewHolder(
        private val binding: RecyclerViewMemeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UIMeme) {
            binding.image.setImage(item.location)
        }
    }
}

private val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<UIMeme>() {
    override fun areItemsTheSame(oldItem: UIMeme, newItem: UIMeme): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UIMeme, newItem: UIMeme): Boolean {
        return oldItem == newItem
    }
}