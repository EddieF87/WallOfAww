package com.eddief.android.infiniteaww.ui.img

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.eddief.android.infiniteaww.model.AwwImage
import com.eddief.android.infiniteaww.data.AwwRoomDB
import com.eddief.android.infiniteaww.data.ImageDao
import com.eddief.android.infiniteaww.data.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImgViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repo.getInstance(application)
    private val mImageDao: ImageDao = AwwRoomDB.getDatabase(application).imageDao()

    suspend fun fetchAwwImages(isFavorites: Boolean) =
        if (isFavorites) repo.getFavorites().data else repo.repoImages()

    fun saveAwwImage(awwImage: AwwImage) {
        GlobalScope.launch(Dispatchers.IO) {
            mImageDao.insert(awwImage)
        }
    }

    fun removeAwwImage(awwImage: AwwImage) {
        GlobalScope.launch(Dispatchers.IO) {
            mImageDao.remove(awwImage.primaryUrl)
        }
    }

    suspend fun isImageSaved(awwImage: AwwImage) = withContext(Dispatchers.IO) {
        mImageDao.checkIfExists(awwImage.primaryUrl).isEmpty()
    }

}