package com.cmput301w20t23.newber.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Singleton class for accessing the Firestore database
 */
public class DatabaseAdapter extends Observable {
    private FirebaseFirestore db = null;
    private CollectionReference users = null;
    private CollectionReference rideRequests = null;
    private CollectionReference ratings = null;

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

    public void createRideRequest(String rider, final RideRequest rideRequest) {
        rideRequests.document(rideRequest.getRequestId())
                .set(rideRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Creating Ride Request successfully written!");
                        setChanged();
                        notifyObservers(rideRequest);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while creating ride request: " + e);
                        clearChanged();
                    }
                });

        users.document(rider)
                .update("currentRequestId", rideRequest.getRequestId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Updating user requestId successfully written!");
                        setChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while updating user requestId: " + e);
                        clearChanged();
                    }
                });

    }

    public void removeRideRequest(RideRequest rideRequest) {
        rideRequests.document(rideRequest.getRequestId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Deleting Ride Request successfully written!");
                        setChanged();
                        notifyObservers(null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while deleting ride request: " + e);
                        clearChanged();
                    }
                });

    }

    public void updateRideRequest(final RideRequest rideRequest) {
        rideRequests.document(rideRequest.getRequestId())
                .set(rideRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Updating Ride Request successfully written!");
                        setChanged();
                        notifyObservers(rideRequest);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while updating ride request: " + e);
                        clearChanged();
                    }
                });

    }

    public void getPendingRideRequests(final Callback<ArrayList<RideRequest>> callback) {
        rideRequests.whereEqualTo("driver", null)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<RideRequest> rideRequests = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                rideRequests.add(document.toObject(RideRequest.class));
                            }
                        }

                        callback.myResponseCallback(rideRequests);
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
                        System.out.println("Adding role data written!");
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

    public void getUser(String uid, final Callback<Map<String, Object>> callback) {
        System.out.println(uid);
        DocumentReference docRef = users.document(uid);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                Map<String, Object> map = new HashMap<>();
                map.put("user", user);
                map.put("role", documentSnapshot.get("role"));

                callback.myResponseCallback(map);
            }
        });
    }

    public void checkUserName(String username, final Callback<Boolean> callback) {
        users.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    System.out.println("Username received:" + document.getData());
                                }

                                callback.myResponseCallback(true);
                            } else {
                                callback.myResponseCallback(false);
                            }
                        }
                    }
                });
    }

    public void getRating(String uid, final Callback<Rating> callback) {
        DocumentReference docRef = ratings.document(uid);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Rating rating = documentSnapshot.toObject(Rating.class);
                        callback.myResponseCallback(rating);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.myResponseCallback(null);
                    }
                });
    }

    public void getRideRequest(String requestId, final Callback<RideRequest> callback) {
        DocumentReference docRef = rideRequests.document(requestId);

//         Trying to add a listener, did not work. Not sure why
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    RideRequest rideRequest = documentSnapshot.toObject(RideRequest.class);

                    callback.myResponseCallback(rideRequest);
                    setChanged();
                    notifyObservers(rideRequest);
                }
            }
        });
    }

    public void updateUserInfo(String uid, String newEmail, String newPhone) {
        Map<String, Object> newData = new HashMap<>();
        newData.put("phone", newPhone);
        newData.put("email", newEmail);

        users.document(uid)
                .update(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Updating user successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while updating user: " + e);
                    }
                });
    }
}
