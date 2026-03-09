package com.example.tracks;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseServices {

    private static FirebaseServices instance;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private Uri selectedImageURL;
    private User currentUser;

    private FirebaseServices() {

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static FirebaseServices getInstance() {

        if(instance == null)
            instance = new FirebaseServices();

        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getFire() {
        return firestore;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public Uri getSelectedImageURL() {
        return selectedImageURL;
    }

    public void setSelectedImageURL(Uri selectedImageURL) {
        this.selectedImageURL = selectedImageURL;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public void getCurrentObjectUser(UserCallback callback) {

        if(auth.getCurrentUser() == null){
            callback.onUserLoaded(null);
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if(documentSnapshot.exists()){

                        User user = documentSnapshot.toObject(User.class);
                        currentUser = user;
                        callback.onUserLoaded(user);

                    }else{
                        callback.onUserLoaded(null);
                    }

                })
                .addOnFailureListener(e -> callback.onUserLoaded(null));
    }

    public void updateUser(User user){

        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(uid)
                .update(
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "username", user.getUsername(),
                        "address", user.getAddress(),
                        "phone", user.getPhone(),
                        "photo", user.getPhoto(),
                        "favorites", user.getFavorites()
                )
                .addOnSuccessListener(aVoid -> currentUser = user)
                .addOnFailureListener(e -> {});
    }

    public interface UserCallback{
        void onUserLoaded(User user);
    }
}