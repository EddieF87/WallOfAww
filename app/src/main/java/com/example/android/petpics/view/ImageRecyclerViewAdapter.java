package com.example.android.petpics.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.petpics.R;
import com.example.android.petpics.model.AwwImage;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageRecyclerViewAdapter extends RecyclerView.Adapter<ImageRecyclerViewAdapter.ImageViewHolder> {

    private final List<AwwImage> imageURLs;
    private ItemClickListener mClickListener;

    public ImageRecyclerViewAdapter(List<AwwImage> imageURLs) {
        this.imageURLs = imageURLs;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_img, parent, false);
        return new ImageViewHolder(relativeLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        AwwImage awwImageData = imageURLs.get(position);
        String imageThumbnail = awwImageData.getThumbnail();

        holder.mView.setTag(awwImageData);
        Picasso.get().load(imageThumbnail)
                .placeholder(R.drawable.ic_paw)
                .error(R.drawable.ic_broken_img)
                .into(holder.imageView);
        if(awwImageData.isVideo()) {
            holder.gifView.setVisibility(View.VISIBLE);
        } else {
            holder.gifView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(imageURLs == null) {
            return 0;
        } else {
            return imageURLs.size();
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final ImageView imageView;
        private final TextView gifView;
        private final View mView;

        ImageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.image);
            gifView = itemView.findViewById(R.id.gif);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AwwImage awwImageData = (AwwImage) mView.getTag();
            if (mClickListener != null) mClickListener.onItemClick(awwImageData);
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(AwwImage awwImageData);
    }

}
