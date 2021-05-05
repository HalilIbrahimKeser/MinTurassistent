package com.aphex.minturassistent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.R;
import com.aphex.minturassistent.viewmodel.ViewModel;


public class Adapter extends ListAdapter<Trip, ViewHolder> {
    private LayoutInflater layoutInflater;
    private ViewModel mViewModel;
    Context context;


    public Adapter(Context context, @NonNull DiffUtil.ItemCallback<Trip> diffCallback) {
        super(diffCallback);
        layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item, null);

        ViewHolder holder = new ViewHolder(view, new ViewHolder.MyClickListener() {
            @Override
            public void onEdit(int position, View view) {
                Trip click = getItem(position);
                //int UserId = click.getmUserId();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip current = getItem(position);
        String title = "Tittel: " + current.getmTripName();
        String place = "Lokasjon: " + current.getmPlace();
        holder.textViewTitle.setText(title);
        holder.textViewPlace.setText(place);
        holder.textViewDate.setText(current.getmDate());
    }

    public static class WordDiff extends DiffUtil.ItemCallback<Trip> {
        @Override
        public boolean areItemsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem == newItem;
        }
        @Override
        public boolean areContentsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return true;
            //return oldItem.getmName().equals(newItem.getmName());
        }
    }
}


class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    MyClickListener listener;
    public final TextView textViewTitle;
    public final TextView textViewPlace;
    public final TextView textViewDate;


    ViewHolder(View itemView, MyClickListener listener) {
        super(itemView);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewPlace = itemView.findViewById(R.id.textViewPlace);
        textViewDate = itemView.findViewById(R.id.textViewDate);

        this.listener = listener;

        textViewTitle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewTitle:
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