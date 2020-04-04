package com.cmput301w20t23.newber.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.UserController;
import com.google.firebase.auth.FirebaseAuth;

/**
 * The Android Activity that handles user sign-up.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private UserController userController;
    private String role;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private String password;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userController = new UserController(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Initiate signing up of the user when the appropriate button is clicked.
     *
     * @param view the button that was clicked
     */
// sign up user, called if "Sign Up" button is clicked
    public void signUp(View view) {

        // get selected radio button from group
        int roleId = ((RadioGroup) findViewById(R.id.radio_role)).getCheckedRadioButtonId();

        // if no radio button is clicked, assign empty string to role, otherwise assign button text
        role = (roleId == -1) ? "" : ((RadioButton) (findViewById(roleId))).getText().toString();

        firstName = ((EditText) (findViewById(R.id.user_first_name_sign_up))).getText().toString();
        lastName = ((EditText) (findViewById(R.id.user_last_name_sign_up))).getText().toString();
        username = ((EditText) (findViewById(R.id.username_sign_up))).getText().toString();
        phone = ((EditText) (findViewById(R.id.phone_sign_up))).getText().toString();
        email = ((EditText) (findViewById(R.id.email_sign_up))).getText().toString();
        password = ((EditText) (findViewById(R.id.password_sign_up))).getText().toString();
        confirmPassword = ((EditText) (findViewById(R.id.confirm_password_sign_up))).getText().toString();

        // if user inputs are valid
        if (userController.isSignUpValid(role, firstName, lastName, username, phone, email, password, confirmPassword)) {
            userController.createUser(role, firstName, lastName, username, phone, email, password);
        }
    }

    /**
     * Switch to LoginActivity when the appropriate link is clicked.
     *
     * @param view the TextView link that was clicked
     */
// transition to LoginActivity, called if "Already registered? LOGIN" is clicked
    public void login(View view) {
        Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
        finish();
    }
}
