package com.example.tracks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<TrackItem> trackList;
    private OnItemClickListener itemClickListener;
    private OnFavoriteClickListener favoriteClickListener;

    public MyAdapter(Context context, ArrayList<TrackItem> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,int position){
        TrackItem track = trackList.get(position);

        holder.trackName.setText(track.getTrackName());
        holder.raceDistance.setText("Distance: " + track.getRaceDistance());
        holder.numberOfLaps.setText("Laps: " + track.getNumberOfLaps());
        holder.firstGrandPrix.setText("First GP: " + track.getFirstGrandPrix());

        if (track.getImageUrl() == null || track.getImageUrl().isEmpty()) {
            Picasso.get().load(R.drawable.ic_launcher_foreground).into(holder.trackImage);
        } else {
            Picasso.get().load(track.getImageUrl()).into(holder.trackImage);
        }

        holder.favoriteIcon.setImageDrawable(
                ContextCompat.getDrawable(context,
                        track.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star));

        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });

        holder.favoriteIcon.setOnClickListener(v -> {
            track.setFavorite(!track.isFavorite());
            holder.favoriteIcon.setImageDrawable(
                    ContextCompat.getDrawable(context,
                            track.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star));

            if (!track.isFavorite() && favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClick(track);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView trackName, raceDistance, numberOfLaps, firstGrandPrix;
        ImageView trackImage, favoriteIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.tvTrackName);
            raceDistance = itemView.findViewById(R.id.tvRaceDistance);
            numberOfLaps = itemView.findViewById(R.id.tvNumberOfLaps);
            firstGrandPrix = itemView.findViewById(R.id.tvFirstGrandPrix);
            trackImage = itemView.findViewById(R.id.ivStadiumImage);
            favoriteIcon = itemView.findViewById(R.id.ivFavoriteIcon);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(TrackItem track);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favoriteClickListener = listener;
    }
}