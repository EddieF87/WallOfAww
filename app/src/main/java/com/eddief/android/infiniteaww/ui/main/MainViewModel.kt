package com.eddief.android.infiniteaww.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.eddief.android.infiniteaww.R
import com.eddief.android.infiniteaww.model.AwwImage
import com.eddief.android.infiniteaww.data.Repo
import com.eddief.android.infiniteaww.model.Resource
import com.eddief.android.infiniteaww.model.SubReddit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val COLUMNS = "columns_key"

        val subReddits: List<SubReddit> = listOf(
            SubReddit(R.id.aww, "aww", true),
            SubReddit(R.id.rarepuppers, "rarepuppers", true),
            SubReddit(R.id.eyebleach, "eyebleach", true),
            SubReddit(R.id.corgi, "corgi"),
            SubReddit(R.id.kitty, "kitty"),
            SubReddit(R.id.foxes, "foxes"),
            SubReddit(R.id.pomeranians, "pomeranians"),
            SubReddit(R.id.cats, "cats"),
            SubReddit(R.id.rabbits, "rabbits"),
            SubReddit(R.id.redpandas, "redpandas"),
            SubReddit(R.id.dogpictures, "dogpictures"),
            SubReddit(R.id.catpictures, "catpictures"),
            SubReddit(R.id.cute, "cute")
        )
    }

    private val mRepo = Repo.getInstance(application)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private var subsPath = ""

    private var mAwwImageData = MutableLiveData<Resource<List<AwwImage>>>()
    val awwImageData: LiveData<Resource<List<AwwImage>>> get() = mAwwImageData

    var isFavorites = false
        private set(value) {
            isFavoritesLiveData.value = value
            field = value
        }
    val isFavoritesLiveData = MutableLiveData(isFavorites)

    val canScrollRefresh get() = !isFavorites && mRepo.repoImages().isNotEmpty()

    init {
        updatePath()
    }

    private fun updatePath() {
        fun MutableList<String>.setSub(subReddit: SubReddit) {
            if (sharedPreferences.getBoolean(subReddit.pref.toString(), subReddit.default)) {
                this.add(subReddit.sub)
            }
        }

        val subs: MutableList<String> = ArrayList()
        subReddits.forEach {
            subs.setSub(it)
        }
        subsPath = subs.toString()
            .replace(", ", "+")
            .replace("[", "")
            .replace("]", "")
    }

    fun parseSubreddits(callback: (checked: ArrayList<Int>, unchecked: ArrayList<Int>) -> Unit) {
        val checked = ArrayList<Int>()
        val unchecked = ArrayList<Int>()
        subReddits.forEach {
            if (sharedPreferences.getBoolean(it.pref.toString(), it.default)) {
                checked.add(it.pref)
            } else {
                unchecked.add(it.pref)
            }
        }
        callback(checked, unchecked)
    }

    fun saveSubReddits(checked: List<Int>, unchecked: List<Int>) {
        sharedPreferences.edit().apply {
            checked.forEach { putBoolean(it.toString(), true) }
            unchecked.forEach { putBoolean(it.toString(), false) }
            apply()
        }
        updatePath()
    }

    fun getColumns() = sharedPreferences.getInt(COLUMNS, 3)

    fun saveColumns(columns: Int) {
        sharedPreferences.edit()
            .putInt(COLUMNS, columns)
            .apply()
    }

    fun getDataFromRepository() {
        viewModelScope.launch(Dispatchers.Main) {
            setData(mRepo.addMoreImages(subsPath))
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.Main) {
            setData(mRepo.refreshData(subsPath))
        }
    }

    fun getFreshData() {
        if (!isFavorites) {
            viewModelScope.launch(Dispatchers.Main) {
                setData(mRepo.refreshData(subsPath))
            }
        }
    }

    private suspend fun setData(awwImages: Resource<List<AwwImage>>) {
        mAwwImageData.value = if (isFavorites) mRepo.getFavorites() else awwImages
    }

    fun toggleFavorites() {
        isFavorites = !isFavorites
        viewModelScope.launch(Dispatchers.Main) {
            mAwwImageData.value = if (isFavorites) {
                val favorites = mRepo.getFavorites()
                if (favorites.data.isNotEmpty()) {
                    favorites
                } else {
                    isFavorites = false
                    mRepo.getCurrentImages(subsPath)
                }
            } else {
                mRepo.getCurrentImages(subsPath)
            }
        }
    }
}