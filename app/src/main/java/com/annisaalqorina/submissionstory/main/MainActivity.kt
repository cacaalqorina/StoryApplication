package com.annisaalqorina.submissionstory.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.R
import com.annisaalqorina.submissionstory.adapter.StoryAdapter
import com.annisaalqorina.submissionstory.databinding.ActivityMainBinding
import com.annisaalqorina.submissionstory.response.ListStoryItem
import com.annisaalqorina.submissionstory.viewmodel.DataViewModel
import com.annisaalqorina.submissionstory.viewmodel.MainViewModel
import com.annisaalqorina.submissionstory.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class MainActivity : AppCompatActivity() {

    companion object {
        const val IS_SUCCESS = 201
        const val STR_TOKEN = "TOKEN"
    }

    private lateinit var activityMainBinding: ActivityMainBinding

    private val  list = ArrayList<ListStoryItem>()
    private val storyViewModel : MainViewModel by viewModels()

    private lateinit var dataViewModel: DataViewModel
    private var token : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val dataPreferences = DataPreferences.getInstance(dataStore)
        dataViewModel = ViewModelProvider(
            this, ViewModelFactory(dataPreferences)
        )[DataViewModel::class.java]

        activityMainBinding.snapStory.setHasFixedSize(true)

        storyViewModel.listStory.observe(this) { listStory ->
            setStoryResult(listStory)
        }

        storyViewModel.isLoading.observe(this){
            setProgressDialog(it)
        }

        storyViewModel.snackbarText.observe(this) {
            Snackbar.make(
                activityMainBinding.snapStory, it, Snackbar.LENGTH_SHORT
            ).show()
        }

        dataViewModel.getDataSession().observe(
            this
        ){
            token = it.token
            storyViewModel.getStories("Bearer $token")

            setClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_logout -> {
                val alertDialog = this.let {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                    builder.apply {
                        setTitle(context.getString(R.string.confirmation))
                        setMessage(context.getString(R.string.confirmation_desc))
                        setPositiveButton(
                            R.string.yes
                        ) { _, _ ->
                            dataViewModel.clearDataSession()
                            startActivity(Intent(baseContext, SignInActivity::class.java))
                            finish()
                        }
                        setNegativeButton(
                            R.string.no
                        ) { _, _ -> }
                    }
                    builder.create()
                }
                alertDialog.show()
                true
            }
            else -> true
        }
    }

    private fun setClick() {
        activityMainBinding.createAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            intent.putExtra(STR_TOKEN, token)
            launchAddStoryActivity.launch(intent)
        }
    }

    private val launchAddStoryActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == IS_SUCCESS) storyViewModel.getStories("Bearer $token")
    }

    private fun setProgressDialog(isLoading : Boolean) {
        activityMainBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setStoryResult(stories: List<ListStoryItem>) {
        list.clear()
        val listStory = ArrayList<ListStoryItem>()
        for (story in stories) {
            story.apply {
                val getResult = ListStoryItem(
                    id,
                    name,
                    description,
                    photoUrl,
                    createdAt
                )
                listStory.add(getResult)
            }
        }
        list.addAll(listStory)

        if (listStory.isEmpty()) {
            activityMainBinding.dataNon.visibility = View.VISIBLE
        } else {
            activityMainBinding.dataNon.visibility = View.GONE
        }

        setRecycler()
    }

    private fun setRecycler() {
        activityMainBinding.snapStory.layoutManager = LinearLayoutManager(this)
        val listUserAdapter = StoryAdapter(list)
        activityMainBinding.snapStory.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem, optionsCompat: ActivityOptionsCompat) {
                val detailIntent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                val item = ListStoryItem(
                    name = data.name,
                    description = data.description,
                    photoUrl = data.photoUrl,
                    createdAt = data.createdAt
                )
                detailIntent.putExtra(DetailStoryActivity.EXTRA_DATA, item)
                startActivity(detailIntent, optionsCompat.toBundle())
            }
        })
    }
}