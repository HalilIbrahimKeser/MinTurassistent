package com.aphex.minturassistent.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.R;


public class Adapter extends ListAdapter<Trip, ViewHolder> {
    private LayoutInflater layoutInflater;
    Context context;
    String isFinished;
    public MutableLiveData<Trip> mTripPosition;


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

        ViewHolder holder = new ViewHolder(view, (position, view1) -> {
            Trip trip = getItem(position);
            isFinished = trip.getmIsFinished().toString();

            SharedPreferences prefs = view1.getContext().getSharedPreferences("tripID", 0);
            SharedPreferences.Editor editor = prefs.edit();
            prefs.edit().remove("tripID").apply();
            editor.putInt("tripID", trip.getmTripID());
            editor.putFloat("startgeolat", (float) trip.getStartGeo().latitude);
            editor.putFloat("startgeolon", (float) trip.getStartGeo().longitude);
            editor.putFloat("stopgeolat", (float) trip.getStopGeo().latitude1);
            editor.putFloat("stopgeolon", (float) trip.getStopGeo().longitude1);
            editor.apply();

            if (isFinished.contains("true")) {
                Navigation.findNavController(view1).navigate(R.id.detailsFragment);
            }else {
                Navigation.findNavController(view1).navigate(R.id.trackTourFragment);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip current = getItem(position);
        String title = "Tittel: " + current.getmTripName();
        String place = "Lokasjon: " + current.getmPlace();
        isFinished = current.getmIsFinished().toString();

        if (isFinished.contains("true")) {
            holder.imIconStatus.setBackgroundResource(R.drawable.image_finished);
        }else {
            holder.imIconStatus.setBackgroundResource(R.drawable.image_planned);
        }

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
    public final TextView imIconStatus;
    public final CardView cardView;

    ViewHolder(View itemView, MyClickListener listener) {
        super(itemView);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewPlace = itemView.findViewById(R.id.textViewPlace);
        textViewDate = itemView.findViewById(R.id.textViewDate);
        imIconStatus = itemView.findViewById(R.id.imIconStatus);
        cardView = itemView.findViewById(R.id.cardView);
        this.listener = listener;
        cardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cardView:
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