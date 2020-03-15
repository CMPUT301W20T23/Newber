package com.cmput301w20t23.newber.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

/**
 * The Android Activity that handles user login.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private UserController userController;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        userController = new UserController(this);

        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            this.userController.getUser(new Callback<Map<String, Object>>() {
                @Override
                public void myResponseCallback(Map<String, Object> result) {
                    String role = (String) result.get("role");

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }

        else {
            setContentView(R.layout.activity_login);

            // hide action bar
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }
    }

    /**
     * Initiates logging in when the appropriate button is clicked.
     *
     * @param view the button that was clicked
     */
    public void login(View view) {

        email = ((EditText)(findViewById(R.id.email_login))).getText().toString();
        password = ((EditText)(findViewById(R.id.password_login))).getText().toString();

        // if user has input values in both email and password fields
        if (userController.isLoginValid(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // if login was successful
                    if (task.isSuccessful()) {
                        Log.d("MYTAG", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        // transition to main screen after log in
                        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                        finish();
                    }
                    // if login was unsuccessful
                    else {
                        Log.w("MYTAG", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Invalid credentials",
                                Toast.LENGTH_SHORT).show();
                        // clear email and password fields
                        ((EditText)(findViewById(R.id.email_login))).setText("");
                        ((EditText)(findViewById(R.id.password_login))).setText("");
                    }
                }
            });
        }
    }

    /**
     * Switches to SignUpActivity when the appropriate link is clicked.
     *
     * @param view the TextView link that was clicked
     */
    public void signUp(View view) {
        Intent signUpIntent = new Intent(getBaseContext(), SignUpActivity.class);
        signUpIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(signUpIntent);
        finish();
    }

}
