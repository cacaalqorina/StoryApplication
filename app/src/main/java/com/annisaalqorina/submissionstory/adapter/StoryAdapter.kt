package com.annisaalqorina.submissionstory.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.annisaalqorina.submissionstory.databinding.ItemStoryBinding
import com.annisaalqorina.submissionstory.response.ListStoryItem
import com.bumptech.glide.Glide

class StoryAdapter (private val listStory: ArrayList<ListStoryItem>) : RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var optionsCompat: ActivityOptionsCompat

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem, optionsCompat: ActivityOptionsCompat)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    class ListViewHolder (var itemStoryBinding: ItemStoryBinding) : RecyclerView.ViewHolder(itemStoryBinding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemStoryBinding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(itemStoryBinding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = listStory[position]

        holder.itemStoryBinding.apply {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    androidx.core.util.Pair(storyPhoto, "image"),
                    androidx.core.util.Pair(nameAccount, "name"),
                    androidx.core.util.Pair(tvDescription, "description"),
                )

            nameAccount.text = story.name
            tvDescription.text = story.description

            Glide.with(holder.itemView.context)
                .load(story.photoUrl)
                .override(100)
                .into(storyPhoto)

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(listStory[holder.adapterPosition], optionsCompat)
            }
        }
    }
    override fun getItemCount(): Int = listStory.size
}