package com.example.tracks;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class FirebaseServices {

    private static FirebaseServices instance;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    // روابط الصور المختارة
    private Uri selectedImageURL;

    private User currentUser;
    private boolean userChangeFlag;
    private TrackItem selectedTrack;

    public FirebaseServices() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        getCurrentObjectUser(user -> {
            if (user != null) setCurrentUser(user);
        });

        userChangeFlag = false;
        selectedImageURL = null;
    }

    public Uri getSelectedImageURL() { return selectedImageURL; }
    public void setSelectedImageURL(Uri selectedImageURL) { this.selectedImageURL = selectedImageURL; }



    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }

    public TrackItem getSelectedTrack() { return selectedTrack; }
    public void setSelectedTrack(TrackItem selectedTrack) { this.selectedTrack = selectedTrack; }

    public boolean isUserChangeFlag() { return userChangeFlag; }
    public void setUserChangeFlag(boolean userChangeFlag) { this.userChangeFlag = userChangeFlag; }

    // --- SINGLETON ---
    public static FirebaseServices getInstance() {
        if (instance == null) instance = new FirebaseServices();
        return instance;
    }

    public static FirebaseServices reloadInstance() {
        instance = new FirebaseServices();
        return instance;
    }

    public FirebaseAuth getAuth() { return auth; }
    public FirebaseFirestore getFire() { return firestore; }
    public FirebaseStorage getStorage() { return storage; }

    public void getCurrentObjectUser(UserCallback callback) {
        ArrayList<User> usersInternal = new ArrayList<>();
        firestore.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot dataSnapshot : queryDocumentSnapshots.getDocuments()) {
                        User user = dataSnapshot.toObject(User.class);
                        if (auth.getCurrentUser() != null &&
                                auth.getCurrentUser().getEmail().equals(user.getUsername())) {
                            usersInternal.add(user);
                        }
                    }
                    if (!usersInternal.isEmpty()) currentUser = usersInternal.get(0);
                    callback.onUserLoaded(currentUser);
                })
                .addOnFailureListener(e -> {});
    }

    public boolean updateUser(User user) {
        final boolean[] flag = {false};
        String collectionName = "users";
        String usernameValue = user.getUsername();

        Query query = firestore.collection(collectionName)
                .whereEqualTo("username", usernameValue);

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        DocumentReference documentRef = document.getReference();
                        documentRef.update(
                                        "firstName", user.getFirstName(),
                                        "lastName", user.getLastName(),
                                        "username", user.getUsername(),
                                        "address", user.getAddress(),
                                        "phone", user.getPhone(),
                                        "photo", user.getPhoto(),
                                        "favorites", user.getFavorites()
                                ).addOnSuccessListener(aVoid -> flag[0] = true)
                                .addOnFailureListener(e -> System.err.println("Error updating document: " + e));
                    }
                })
                .addOnFailureListener(e -> System.err.println("Error getting documents: " + e));
        return flag[0];
    }

    public interface UserCallback {
        void onUserLoaded(User user);
    }
}