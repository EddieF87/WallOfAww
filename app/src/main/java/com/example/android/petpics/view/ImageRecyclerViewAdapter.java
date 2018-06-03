package com.example.android.petpics.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.example.android.petpics.R;
import com.example.android.petpics.model.RedditPostData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {

    private List<RedditPostData> imageURLs;
    private Context mContext;
    private ItemClickListener mClickListener;

    public ImageRecyclerViewAdapter(List<RedditPostData> imageURLs, Context context) {
        this.imageURLs = imageURLs;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_img, parent, false);
        return new ImageViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        RedditPostData redditPostData = imageURLs.get(position);
        String imageThumbnail = redditPostData.getThumbnail();
//        String imageUrl = redditPostData.getUrl();
        holder.mView.setTag(redditPostData);
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.imageView);
//        Glide.with(mContext).load(imageThumbnail).into(holder.imageView);

        Picasso.get().load(imageThumbnail).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(imageURLs == null) {
            return 0;
        } else {
            return imageURLs.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView;
        private View mView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.image);
            imageView.setOnClickListener(this);
//            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            RedditPostData redditPostData = (RedditPostData) mView.getTag();
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl));
//            mContext.startActivity(browserIntent);
//            Log.d("pusha click", "1" + imageUrl);
            if (mClickListener != null) mClickListener.onItemClick(redditPostData);
        }
    }
//
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(RedditPostData redditPostData);
    }

}
