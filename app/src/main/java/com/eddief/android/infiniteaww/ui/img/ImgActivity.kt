package com.eddief.android.infiniteaww.ui.img

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.lifecycle.lifecycleScope
import com.eddief.android.infiniteaww.R
import com.eddief.android.infiniteaww.databinding.ActivityImgBinding
import kotlinx.coroutines.launch

class ImgActivity : AppCompatActivity() {

    companion object {
        const val IMG_POS_KEY = "img_pos"
        const val IS_FAVS_KEY = "is_favs"
    }

    private val viewModel: ImgViewModel by viewModels()

    private lateinit var imageMenu: Menu
    private lateinit var binding: ActivityImgBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImgBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val startPosition = intent.getIntExtra(IMG_POS_KEY, 0)
        val isFavorites = intent.getBooleanExtra(IS_FAVS_KEY, false)
        lifecycleScope.launch {
            val awwImages = viewModel.fetchAwwImages(isFavorites)
            binding.viewPager.adapter = ImgPagerAdapter(this@ImgActivity, awwImages)
            binding.viewPager.doOnAttach {
                binding.viewPager.setCurrentItem(startPosition, false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_img, menu)
        imageMenu = menu
        return true
    }

    fun setSaveIcon(saved: Boolean) {
        imageMenu.findItem(R.id.action_save).setIcon(
            if (saved) R.drawable.ic_star else R.drawable.ic_save
        )
    }
}