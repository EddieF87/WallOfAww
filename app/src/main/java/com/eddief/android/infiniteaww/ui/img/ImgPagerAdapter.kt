package com.eddief.android.infiniteaww.ui.img

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eddief.android.infiniteaww.model.AwwImage

class ImgPagerAdapter(activity: FragmentActivity, private val awwImages: List<AwwImage>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = awwImages.size

    override fun createFragment(position: Int): Fragment =
        ImgFragment.newInstance(awwImages[position])

}