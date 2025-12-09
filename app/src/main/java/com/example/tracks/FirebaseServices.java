package com.example.tracks;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class FirebaseServices{

private static FirebaseServices instance;
private FirebaseAuth auth;
private FirebaseFirestore firestore;
private FirebaseStorage storage;
    private TrackItem selectedTrack;

    private Uri selectedImageURL;
    private User currentUser;

    private boolean userChangeFlag;




    public FirebaseServices(){
    auth = FirebaseAuth.getInstance();
    firestore = FirebaseFirestore.getInstance();
    storage = FirebaseStorage.getInstance();
        selectedImageURL = null;


}

public FirebaseAuth getAuth() {
    return auth;
}

public FirebaseStorage getStorage() {
    return storage;
}


public FirebaseFirestore getFirestore() {
    return firestore;
}

    public static FirebaseServices reloadInstance(){
        instance=new FirebaseServices();
        return instance;
    }

    public static void setInstance(FirebaseServices instance) {
        FirebaseServices.instance = instance;
    }
    public Uri getSelectedImageURL() {
        return selectedImageURL;
    }

    public void setSelectedImageURL(Uri selectedImageURL)
    {
        this.selectedImageURL = selectedImageURL;
    }
    public  static FirebaseServices getInstance(){
        if (instance==null){
            instance=new FirebaseServices();

        }
        return instance;
    }
    public TrackItem getSelectedTrack()
    {
            return selectedTrack;
    }



    public void setselectedTrack(TrackItem selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public User getCurrentUser()
    {
        return this.currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    public boolean updateUser(User user)
    {
        final boolean[] flag = {false};
        // Reference to the collection
        String collectionName = "users";
        String firstNameFieldName = "firstName";
        String firstNameValue = user.getFirstName();
        String lastNameFieldName = "lastName";
        String lastNameValue = user.getLastName();
        String email = "email";
        String emailvalue = user.getEmail();
        String password = "password";
        String passwordvalue = user.getPassword();
        String phoneFieldName = "phone";
        String phoneValue = user.getPhone();
        String photoFieldName = "photo";
        String photoValue = user.getPhoto();
        String favoritesFieldName = "favorites";
        ArrayList<String> favoritesValue = user.getFavorites();


        // Create a query for documents based on a specific field
        Query query = firestore.collection(collectionName).
                whereEqualTo(email, emailvalue);

        // Execute the query
        query.get()
                .addOnSuccessListener((QuerySnapshot querySnapshot) -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        // Get a reference to the document
                        DocumentReference documentRef = document.getReference();

                        // Update specific fields of the document
                        documentRef.update(
                                        firstNameFieldName, firstNameValue,
                                        lastNameFieldName, lastNameValue,
                                        phoneFieldName, phoneValue,
                                        photoFieldName, photoValue,
                                        email, emailvalue,
                                        password,passwordvalue,
                                        favoritesFieldName, favoritesValue

                                )
                                .addOnSuccessListener(aVoid -> {

                                    flag[0] = true;
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Error updating document: " + e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error getting documents: " + e);
                });

        return flag[0];
    }


}


