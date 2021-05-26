package com.aphex.minturassistent.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.TripImages;
import com.aphex.minturassistent.R;
import com.bumptech.glide.Glide;

public class ImageAdapter extends ListAdapter<TripImages, ImageViewHolder> {
    private final LayoutInflater layoutInflater;
    Context context;

    public ImageAdapter(Context context, @NonNull DiffUtil.ItemCallback<TripImages> diffCallback) {
        super(diffCallback);
        layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.recyclerview_images_item, null);

        return new ImageViewHolder(view, (position, view1) -> {
            TripImages tripImage = getItem(position);
            SharedPreferences prefs = view1.getContext().getSharedPreferences("tripImage", 0);
            SharedPreferences.Editor editor = prefs.edit();
            prefs.edit().remove("tripImage").apply();
            editor.putInt("tripID", tripImage.mTripID);
            editor.putString("imgUrl", tripImage.mImageURI);
            editor.apply();

            Navigation.findNavController(view1).navigate(R.id.storedSingleImageShowFragment);
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        TripImages current = getItem(position);
        String imageTitle = "Bildetittel: " + current.mTitle;
        String test = current.mImageURI;

        Glide.with(holder.ivThumb)
                .load(current.mImageURI)
                .thumbnail(0.33f)
                .centerCrop()
                .into(holder.ivThumb);
        if (current.mTitle == null) {
            holder.tvImageTitle.setText(R.string.str_IngenBilder);
            holder.tvImageDate.setText(R.string.str_IngenBilder1);
        } else {
            holder.tvImageTitle.setText(imageTitle);
//            holder.tvImageDate.setText(current.mDate);
            holder.tvImageDate.setText(current.mImageURI);
        }
    }

    public static class ImageDiff extends DiffUtil.ItemCallback<TripImages> {
        @Override
        public boolean areItemsTheSame(@NonNull TripImages oldItem, @NonNull TripImages newItem) {
            return oldItem == newItem;
        }
        @Override
        public boolean areContentsTheSame(@NonNull TripImages oldItem, @NonNull TripImages newItem) {
            return true;
        }
    }
}

class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    MyClickListener listener;
    public TextView tvImageTitle;
    public TextView tvImageDate;
    public ImageView ivThumb;
    public CardView imageCardView;

    ImageViewHolder(View itemView, MyClickListener listener) {
        super(itemView);
        tvImageTitle = itemView.findViewById(R.id.tvImageTitle);
        tvImageDate = itemView.findViewById(R.id.tvImageDate);
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