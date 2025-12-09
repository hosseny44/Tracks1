package com.example.tracks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tracks.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.MyViewHolder> {

    Context context;
    ArrayList<F1Track> trackList;

    public TrackListAdapter(Context context, ArrayList<F1Track> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        F1Track track = trackList.get(position);

        holder.trackName.setText("Track Name: " + track.getTrackName());
        holder.raceDistance.setText("Race Distance: " + track.getRaceDistance() + " Km");
        holder.numberOfLaps.setText("Number Of Laps: " + track.getNumberOfLaps());
        holder.firstGrandPrix.setText("First Grand Prix: " + track.getFirstGrandPrix());

        if (track.getImageUrl() == null || track.getImageUrl().isEmpty()) {
            Picasso.get().load(R.drawable.ic_launcher_foreground).into(holder.trackImage);
        } else {
            Picasso.get().load(track.getImageUrl()).into(holder.trackImage);
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putParcelable("track", track);

            // creat Fragment
            TrackDetailsFragment td = new TrackDetailsFragment();
            td.setArguments(args);

            // Replace Fragment
            FragmentTransaction ft = ((MainActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction();
            ft.replace(R.id.frameLayout, td);
            ft.addToBackStack(null); // ضروري عشان زر العودة يشتغل
            ft.commit();
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView trackName, raceDistance, numberOfLaps, firstGrandPrix;
        ImageView trackImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.tvTrackName);
            raceDistance = itemView.findViewById(R.id.tvRaceDistance);
            numberOfLaps = itemView.findViewById(R.id.tvNumberOfLaps);
            firstGrandPrix = itemView.findViewById(R.id.tvFirstGrandPrix);
            trackImage = itemView.findViewById(R.id.ivStadiumImage);
        }
    }
}
