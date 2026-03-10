package com.example.tracks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FavFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private SearchView srchView;
    private View emptyView;

    private ArrayList<F1Track> allTracks; // كل الحلبات
    private ArrayList<F1Track> favTracks; // الحلبات المفضلة

    private FirebaseServices fbs;
    private FirebaseFirestore db;

    public FavFragment() {}

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        recyclerView = getView().findViewById(R.id.rvfavTracklist);
        srchView = getView().findViewById(R.id.srchViewfavoriteFragment);
        emptyView = getView().findViewById(R.id.empty_view);

        fbs = FirebaseServices.getInstance();
        db = FirebaseFirestore.getInstance();

        allTracks = new ArrayList<>();
        favTracks = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyAdapter(getActivity(), favTracks, "list");
        recyclerView.setAdapter(adapter);

        loadAllTracks();

        // البحث
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

    private void loadAllTracks() {
        db.collection("Tracks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allTracks.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        F1Track track = doc.toObject(F1Track.class);
                        allTracks.add(track);
                    }
                    filterFavorites();
                })
                .addOnFailureListener(e -> Log.e("FavFragment", "Error loading tracks", e));
    }

    private void filterFavorites() {
        favTracks.clear();

        fbs.getCurrentObjectUser(u -> {
            if (u != null && u.getFavorites() != null) {
                for (F1Track t : allTracks) {
                    if (u.getFavorites().contains(t.getId())) {
                        t.setFavorite(true);
                        favTracks.add(t);
                    }
                }
            }

            // Empty state
            if (favTracks.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }

            adapter.updateList(favTracks);
        });
    }

    private void applyFilter(String query) {
        if (query.trim().isEmpty()) {
            filterFavorites();
            return;
        }

        ArrayList<F1Track> filteredList = new ArrayList<>();
        for (F1Track track : favTracks) {
            if (track.getCountryName().toLowerCase().contains(query.toLowerCase()) ||
                    track.getEXP().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(track);
            }
        }

        adapter.updateList(filteredList);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }
}