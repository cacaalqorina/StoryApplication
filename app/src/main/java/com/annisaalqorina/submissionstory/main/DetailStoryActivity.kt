package com.annisaalqorina.submissionstory.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.annisaalqorina.submissionstory.databinding.ActivityDetailStoryBinding
import com.annisaalqorina.submissionstory.response.ListStoryItem
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DATA = "extra_data_story"
    }

    private lateinit var activityDetailStoryBinding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(activityDetailStoryBinding.root)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem
        activityDetailStoryBinding.apply {
            story.apply {
                nameAccount.text = name
                tvDescription.text = description

                Glide.with(baseContext)
                    .load(photoUrl)
                    .override(500)
                    .into(storyPhoto)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = story.name
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}