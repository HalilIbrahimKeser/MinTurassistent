package com.aphex.minturassistent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Images;
import com.aphex.minturassistent.R;
import com.aphex.minturassistent.viewmodel.ViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;


public class ImageAdapter extends ListAdapter<Images, ImageViewHolder> {
    private LayoutInflater layoutInflater;
    private ViewModel mViewModel;
    Context context;


    public ImageAdapter(Context context, @NonNull DiffUtil.ItemCallback<Images> diffCallback) {
        super(diffCallback);
        layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerview_images_item, null);

        ImageViewHolder holder = new ImageViewHolder(view, new ImageViewHolder.MyClickListener() {
            @Override
            public void onEdit(int position, View view) {
//                Image click = getItem(position);
//                int ImageID = click.getmImageID();
//                SharedPreferences prefs = view.getContext().getSharedPreferences("tripID", 0);
//                SharedPreferences.Editor editor = prefs.edit();
//                prefs.edit().remove("tripID").apply();
//                editor.putInt("tripID", tripID);
//                editor.apply();

//                Navigation.findNavController(view).navigate(R.id.detailsFragment);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Images current = getItem(position);
        String imageTitle = "Bildetittel: " + current.mTitle;
        
        Glide.with(holder.ivThumb)
                .load(current.mImageURI)
                .thumbnail(0.33f)
                .centerCrop()
                .into(holder.ivThumb);

        holder.tvImageTitle.setText(imageTitle);
    }

    public static class WordDiff extends DiffUtil.ItemCallback<Images> {
        @Override
        public boolean areItemsTheSame(@NonNull Images oldItem, @NonNull Images newItem) {
            return oldItem == newItem;
        }
        @Override
        public boolean areContentsTheSame(@NonNull Images oldItem, @NonNull Images newItem) {
            return true;
        }
    }
}

class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    MyClickListener listener;

    public TextView tvImageTitle;
    public ImageView ivThumb;
    public CardView imageCardView;

    ImageViewHolder(View itemView, MyClickListener listener) {
        super(itemView);

        tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
        ivThumb = itemView.findViewById(R.id.ivThumb);
        imageCardView = itemView.findViewById(R.id.imageCardView);
        this.listener = listener;

        imageCardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageCardView:
                listener.onEdit(this.getLayoutPosition(), view);
                break;
            default:
                break;
        }
    }
    public interface MyClickListener {

        void onEdit(int p, View view);
    }
}