package com.example.tracks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FavFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private SearchView srchView;
    private ArrayList<TrackItem> tracks, filteredList;
    private FirebaseServices fbs;

    public FavFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        View view = getView();
        if (view == null) return;

        recyclerView = view.findViewById(R.id.rvTracklist);
        srchView = view.findViewById(R.id.srchTrack);
        fbs = FirebaseServices.getInstance();

        tracks = new ArrayList<>();
        filteredList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        myAdapter = new MyAdapter(getActivity(), tracks);
        recyclerView.setAdapter(myAdapter);

        // النقر على الكارد
        myAdapter.setOnItemClickListener(position -> {
            TrackItem selected = tracks.get(position);
            Toast.makeText(getActivity(), "Clicked: " + selected.getTrackName(), Toast.LENGTH_SHORT).show();
            openTrackDetails(selected);
        });

        // النقر على النجمة
        myAdapter.setOnFavoriteClickListener(track -> {
            if(!track.isFavorite()){
                removeTrackFromList(track); // إزالة العنصر من قائمة المفضلة مباشرة
            }
        });

        loadTracksFromFirebase();

        // SearchView listener
        srchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFilter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilter(newText);
                return true;
            }
        });
    }

    private void loadTracksFromFirebase() {
        fbs.getFirestore().collection("tracks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        tracks.clear();
                        User u = fbs.getCurrentUser();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TrackItem track = document.toObject(TrackItem.class);
                            if (u != null && u.getFavorites().contains(track.getTrackName())) {
                                track.setFavorite(true); // وضع حالة المفضلة
                                tracks.add(track);
                            }
                        }

                        myAdapter.notifyDataSetChanged();

                        if (tracks.isEmpty()) {
                            showNoDataDialogue();
                        }

                    } else {
                        Log.e("FavFragment", "Error getting documents", task.getException());
                    }
                });
    }

    private void applyFilter(String query) {
        filteredList = new ArrayList<>();

        if (query.trim().isEmpty()) {
            myAdapter = new MyAdapter(getActivity(), tracks);
            recyclerView.setAdapter(myAdapter);
            myAdapter.setOnItemClickListener(position -> openTrackDetails(tracks.get(position)));
            myAdapter.setOnFavoriteClickListener(track -> {
                if(!track.isFavorite()){
                    removeTrackFromList(track);
                }
            });
            return;
        }

        for (TrackItem track : tracks) {
            if (track.getTrackName().toLowerCase().contains(query.toLowerCase()) ||
                    track.getRaceDistance().toLowerCase().contains(query.toLowerCase()) ||
                    track.getNumberOfLaps().toLowerCase().contains(query.toLowerCase()) ||
                    track.getFirstGrandPrix().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(track);
            }
        }

        if (filteredList.isEmpty()) {
            showNoDataDialogue();
            return;
        }

        myAdapter = new MyAdapter(getActivity(), filteredList);
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(position -> openTrackDetails(filteredList.get(position)));
        myAdapter.setOnFavoriteClickListener(track -> {
            if(!track.isFavorite()){
                removeTrackFromList(track);
            }
        });
    }

    private void openTrackDetails(TrackItem track) {
        Bundle args = new Bundle();
        args.putParcelable("track", track);
        TrackDetailsFragment cd = new TrackDetailsFragment();
        cd.setArguments(args);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, cd);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void showNoDataDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("No Results");
        builder.setMessage("Try again!");
        builder.show();
    }

    // إزالة العنصر من قائمة المفضلة
    public void removeTrackFromList(TrackItem track){
        tracks.remove(track);
        myAdapter.notifyDataSetChanged();
    }

    public void gotoAddTrackFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new AddTrackFragment());
        ft.commit();
    }
}