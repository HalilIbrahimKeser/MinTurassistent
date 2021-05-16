package com.aphex.minturassistent.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.aphex.minturassistent.DetailsFragment;
import com.aphex.minturassistent.Entities.Location;
import com.aphex.minturassistent.Entities.Trip;
import com.aphex.minturassistent.MainActivity;
import com.aphex.minturassistent.R;
import com.aphex.minturassistent.viewmodel.ViewModel;


public class Adapter extends ListAdapter<Trip, ViewHolder> {
    private LayoutInflater layoutInflater;
    private ViewModel mViewModel;
    Context context;
    String isFinished;


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
                Trip current = getItem(position);
                isFinished = current.getmIsFinished().toString();
                Trip click = getItem(position);
                int tripID = click.getmTripID();
                SharedPreferences prefs = view.getContext().getSharedPreferences("tripID", 0);
                SharedPreferences.Editor editor = prefs.edit();
                prefs.edit().remove("tripID").apply();
                editor.putInt("tripID", tripID);
                editor.apply();

                if (isFinished.contains("true")) {
                    Navigation.findNavController(view).navigate(R.id.detailsFragment);
                }else {
                    Navigation.findNavController(view).navigate(R.id.planTourFragment);
                }
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