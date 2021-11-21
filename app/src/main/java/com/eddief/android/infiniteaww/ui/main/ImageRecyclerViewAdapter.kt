package com.eddief.android.infiniteaww.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.eddief.android.infiniteaww.R
import com.eddief.android.infiniteaww.model.AwwImage
import com.eddief.android.infiniteaww.ui.main.ImageRecyclerViewAdapter.ImageViewHolder
import com.squareup.picasso.Picasso

class ImageRecyclerViewAdapter(private val imageURLs: MutableList<AwwImage>) :
    RecyclerView.Adapter<ImageViewHolder>() {

    private var mClickListener: ItemClickListener? = null

    fun updateImages(awwImages: List<AwwImage>) {
        imageURLs.apply {
            clear()
            addAll(awwImages)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val relativeLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_img, parent, false) as RelativeLayout
        return ImageViewHolder(relativeLayout)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageURLs[position], position)
    }

    override fun getItemCount(): Int {
        return imageURLs.size
    }

    inner class ImageViewHolder(view: View) : ViewHolder(view), View.OnClickListener {
        private val gifView: TextView = itemView.findViewById(R.id.gif)
        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private var position: Int? = null

        init {
            imageView.setOnClickListener(this)
        }

        fun bind(awwImageData: AwwImage, position: Int) {
            this.position = position
            Picasso.get().load(awwImageData.thumbnail)
                .placeholder(R.drawable.ic_paw)
                .error(R.drawable.ic_broken_img)
                .into(imageView)
            gifView.isVisible = awwImageData.isVideo
        }

        override fun onClick(view: View) {
            position?.let {
                mClickListener?.onItemClick(it)
            }
        }
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}