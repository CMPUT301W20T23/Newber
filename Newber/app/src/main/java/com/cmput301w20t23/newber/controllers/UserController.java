package com.cmput301w20t23.newber.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cmput301w20t23.newber.database.DatabaseAdapter;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;
import com.cmput301w20t23.newber.views.MainActivity;
import com.cmput301w20t23.newber.views.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

/**
 * Controller class for handling all user-related data processing.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class UserController {
    private Context context;
    private FirebaseAuth mAuth;
    private DatabaseAdapter databaseAdapter;

    /**
     * Instantiates a new UserController.
     *
     * @param context the Android context
     */
    public UserController(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.databaseAdapter = DatabaseAdapter.getInstance();
    }

    /**
     * Checks that the login fields are nonempty.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return a boolean indicating whether or not the Login fields are valid
     */
    public boolean isLoginValid(String email, String password) {
        if ((email.trim()).length() == 0 | password.trim().length() == 0) {
            Toast.makeText(context, "Please enter a username and password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Checks that the Sign Up fields are valid, i.e. nonempty and having proper format.
     *
     * @param role            the user's role
     * @param firstName       the user's first name
     * @param lastName        the user's last name
     * @param username        the user's username
     * @param phone           the user's phone
     * @param email           the user's email
     * @param password        the user's password
     * @param confirmPassword the user's password entered again to confirm
     * @return a boolean indicating whether or not the Sign Up fields are valid
     */
    // check if all fields contain values in sign up form
    public boolean isSignUpValid(String role, String firstName, String lastName, String username, String phone, String email, String password, String confirmPassword) {
        if (firstName.trim().length() == 0 | lastName.trim().length() == 0 |
                username.trim().length() == 0 | phone.trim().length() == 0 | email.trim().length() == 0 |
                password.trim().length() == 0 | confirmPassword.trim().length() == 0) {
            Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (role.trim().length() == 0) {
            Toast.makeText(context, "Please select an account type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("^[+]?[0-9]{10,13}$")) {
            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!confirmPassword.equals(password)) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Logs a user out of the app through Firebase authentication.
     */
    public void logout() {
        mAuth.signOut();
    }

    /**
     * Creates a new user in the database.
     *
     * @param role      the user's role
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param username  the user's username
     * @param phone     the user's phone
     * @param email     the user's email
     */
    public void createUser(final String role,
                           final String firstName,
                           final String lastName,
                           final String username,
                           final String phone,
                           final String email,
                           final String password)
    {
        this.databaseAdapter.checkUserName(username, new Callback<Boolean>() {
            @Override
            public void myResponseCallback(Boolean result) {
                if (result) {
                    Toast.makeText(context, "Username has already been taken", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        User newUser = new User(firstName,
                                                lastName,
                                                username,
                                                phone,
                                                email,
                                                task.getResult().getUser().getUid());

                                        databaseAdapter.createUser(newUser, role);

                                        Intent signedUpIntent = new Intent(UserController.this.context,
                                                MainActivity.class);

                                        signedUpIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        UserController.this.context.startActivity(signedUpIntent);
                                    } else {
                                        Toast.makeText(UserController.this.context,
                                                task.getException().toString(),
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Checks if the contact info fields are valid, i.e. nonempty and having proper format.
     *
     * @param email the user's email
     * @param phone the user's phone
     * @return a boolean indicating whether or not the contact info fields are valid.
     */
    public boolean isContactInfoValid(String email, String phone, String password) {
        if (phone.trim().length() == 0 | email.trim().length() == 0 | password.trim().length() == 0) {
            Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!phone.matches("^[+]?[0-9]{10,13}$")) {
            Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Updates the user's contact info in the database.
     *
     * @param context  the Android context
     * @param email    the user's new email
     * @param phone    the user's new phone
     * @param password the user's password to re-authenticate in order to change the email
     */
    public void saveContactInfo(final Context context, final String email, final String phone, final String password) {

        final FirebaseUser firebaseUser = mAuth.getCurrentUser();

        databaseAdapter.getUser(mAuth.getCurrentUser().getUid(), new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                final User user = (User) result.get("user");

                // current credential
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), password);

                firebaseUser.reauthenticate(credential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                firebaseUser.updateEmail(email);
                                databaseAdapter.updateUserInfo(user.getUid(), email, phone);
                                //TO DO: Change
                                ((ProfileActivity) context).updatePhoneEmailText(phone, email);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,
                                        "Password incorrect. Email could not be updated.",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
            }
        });
    }

    /**
     * Updates user entry with new currentRequestId
     * @param uid user id
     * @param requestId ride request id
     */
    public void updateUserCurrentRequestId(String uid, String requestId) {
        this.databaseAdapter.setUserCurrentRequestId(uid, requestId);
    }

    /**
     * Updates user entry with contents of the user
     * @param uid user id
     */
    public void removeUserCurrentRequestId(String uid) {
        this.databaseAdapter.setUserCurrentRequestId(uid, "");
    }

    public void getUser(Callback<Map<String, Object>> callback) {
        String uid = mAuth.getCurrentUser().getUid();
        this.databaseAdapter.getUser(uid, callback);
    }

    public void getUser(String uid, Callback<Map<String, Object>> callback) {
        this.databaseAdapter.getUser(uid, callback);
    }

    public void getRating(String uid, Callback<Rating> callback) {
        this.databaseAdapter.getRating(uid ,callback);
    }

    public void transferBalance(RideRequest rideRequest) {
        double cost = rideRequest.getCost();

        this.databaseAdapter.incrementUserBalance(rideRequest.getRider(), -1 * cost);
        this.databaseAdapter.incrementUserBalance(rideRequest.getDriver(), cost);
    }

    public void updateRating(String uid, final Rating rating) {
        this.databaseAdapter.updateRating(uid, rating);
    }
}
