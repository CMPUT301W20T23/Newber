package com.cmput301w20t23.newber.database;

import androidx.annotation.NonNull;

import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class DatabaseAdapter {
    private FirebaseFirestore db = null;
    private CollectionReference users = null;
    private CollectionReference rideRequests = null;
    private CollectionReference ratings = null;

//    private User user;
//    private RideRequest rideRequest;
//    private Rating rating;

    private static DatabaseAdapter databaseAdapter = null;

    protected DatabaseAdapter() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        rideRequests = db.collection("rideRequests");
        ratings = db.collection("ratings");
    }

    public static DatabaseAdapter getInstance() {
        if (databaseAdapter == null) {
            databaseAdapter = new DatabaseAdapter();
        }

        return databaseAdapter;
    }

    public void createRating(String uid) {
        Rating rating = new Rating(0, 0);

        this.ratings.document(uid)
                .set(rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Adding rating successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while adding rating: " + e);
                    }
                });
    }

    public void createUser(User user, String role) {
        Map<String, Object> roleData = new HashMap<>();
        roleData.put("role", role);

        this.users.document(user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Adding user successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while adding user: " + e);
                    }
                });

        // To set the role of the user
        this.users.document(user.getUid())
                .set(roleData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Adding user successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while adding user: " + e);
                    }
                });

        // if the user to be created is a driver, create upvotes and downvotes fields in database
        if (role.equals("Driver")) {
            this.createRating(user.getUid());
        }
    }

    public void setUserCurrentRequestId(String uid, String currentRequestId) {
        this.users.document(uid)
                .update("currentRequestId", currentRequestId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Setting currentRequestId successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while setting currentRequestId: " + e);
                    }
                });
    }

    public void getUser(String uid, final Callback<User> callback) {
        System.out.println("UID we got: " + uid);
        DocumentReference docRef = users.document(uid);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                callback.myResponseCallback(user);
            }
        });

        docRef.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.myResponseCallback(null);
            }
        });

    }

}
