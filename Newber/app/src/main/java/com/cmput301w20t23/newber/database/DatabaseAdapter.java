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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Singleton class for accessing the Firestore database across the entire app
 * @author Ibrahim Aly
 */
public class DatabaseAdapter extends Observable {
    private FirebaseFirestore db = null;

    //A reference to the users collection
    private CollectionReference users = null;

    //A reference to the ride requests collection
    private CollectionReference rideRequests = null;

    //A reference to the ratings collection
    private CollectionReference ratings = null;

    //A listener that can listen to updates of the ride request
    public ListenerRegistration rideRequestListener = null;

    private static DatabaseAdapter databaseAdapter = null;

    protected DatabaseAdapter() {
        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        rideRequests = db.collection("rideRequests");
        ratings = db.collection("ratings");
    }

    /**
     * Returns 1 global instance, sticking with the singleton-design pattern
     * @return
     */
    public static DatabaseAdapter getInstance() {
        if (databaseAdapter == null) {
            databaseAdapter = new DatabaseAdapter();
        }

        return databaseAdapter;
    }

    /**
     * Creates a Rating in Firestore with the given ID as the document name
     * @param uid ID of the driver to create a rating for
     */
    public void createRating(String uid) {
        Rating rating = new Rating(0, 0);

        ratings.document(uid)
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

    /**
     * Creates a new ride request in Firestore
     * @param rider The ID of the rider that created this ride request
     * @param rideRequest The new Ride Request to be saved in Firestore
     */
    public void createRideRequest(String rider, final RideRequest rideRequest) {
        //Create the ride request
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

        //Set the rider's current ride request field to this ride request
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

    /**
     * Deletes a ride request from Firestore when the rider cancels it (when it's pending)
     * @param rideRequest The ride request to be deleted
     */
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

    /**
     * Updates a ride request with updated data fields
     * @param rideRequest The updated ride request, its ID is still the same
     */
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

    /**
     * Returns all ride requests that have a status of PENDING in the call back function
     * @param callback The callback function, which holds the list of ride requests, that will be returned to the views
     */
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

    /**
     * Creates a new user with the specified role
     * @param user New User object that will created in Firestore
     * @param role The role of the user
     */
    public void createUser(User user, String role) {
        Map<String, Object> roleData = new HashMap<>();
        roleData.put("role", role);

        // First, set the entire user in the document with the UID as the document key
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

        // if the user to be created is a driver, create Rating in database
        if (role.equals("Driver")) {
            this.createRating(user.getUid());
        }
    }

    /**
     * Sets the current ride request ID field of the user to the input currentRequestId
     * @param uid the ID of the user
     * @param currentRequestId The ID of the ride request
     */
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

    /**
     * Returns the User object from the ID
     * @param uid ID of the user
     * @param callback Callback function that will contain the User details
     */
    public void getUser(String uid, final Callback<Map<String, Object>> callback) {
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

    /**
     * Checks if the username (during sign-up) is unique or not, and returns a boolean in the callback
     * @param username The username
     * @param callback The callback function that will be returned to handle async calls
     */
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

    /**
     * Retrieves the Rating object of the current Driver, and sends it through the callback
     * @param uid ID of the driver
     * @param callback Callback function that will hold the Rating object
     */
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

    /**
     * Updates the rating of a driver in Firestore
     * @param uid ID of the driver
     * @param rating New Rating object that will be saved
     */
    public void updateRating(String uid, final Rating rating) {
        DocumentReference docRef = ratings.document(uid);
        docRef.set(rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Update rating successful");
                        setChanged();
                        notifyObservers(rating);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while updating rating: " + e);
                        clearChanged();
                    }
                });
    }

    /**
     * Gets A ride request from its ID, and returns it in the callback
     * @param requestId the ID of the ride request
     * @param callback The callback function that holds the returned ride request
     */
    public void getRideRequest(String requestId, final Callback<RideRequest> callback) {
        DocumentReference docRef = rideRequests.document(requestId);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            RideRequest rideRequest = documentSnapshot.toObject(RideRequest.class);

                            callback.myResponseCallback(rideRequest);
                        }
                    }
                });
    }

    /**
     * Adds a Firestore listener to a ride request to handle live updates
     * @param requestId The ID of the ride request that will be listened on
     */
    public void addListenerToRideRequest(String requestId) {
        DocumentReference docRef = rideRequests.document(requestId);

        rideRequestListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    RideRequest rideRequest = documentSnapshot.toObject(RideRequest.class);

                    //Once it has changed, notify the observers with the updated Ride request
                    setChanged();
                    notifyObservers(rideRequest);
                }
            }
        });
    }

    /**
     * Updates the User Details with the new phone number and email address
     * @param uid ID of the user whose details will be updated
     * @param newEmail New email address
     * @param newPhone New phone number
     */
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

    /**
     * Updates the User balance with the argument increment
     * @param uid The ID of the User
     * @param increment The increment to be incremented with
     */
    public void incrementUserBalance(String uid, double increment) {
        users.document(uid)
                .update("balance", FieldValue.increment(increment))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Balance successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while updating balance: " + e);
                    }
                });
    }

    /**
     * Sets the token to the User to register a device to this user
     * @param uid The ID of the user
     * @param token The token
     */
    public void setUserToken(String uid, String token) {
        Map<String, Object> newData = new HashMap<>();

        if (token == null) {
            newData.put("token", FieldValue.delete());
        } else {
            newData.put("token", token);
        }

        users.document(uid)
                .update(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("Token successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error while updating token: " + e);
                    }
                });
    }
}
