package com.cmput301w20t23.newber.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.User;

import java.text.DecimalFormat;
import java.util.Map;

//import com.google.firebase.auth.FirebaseAuth;

/**
 * The Android Activity that contains the user's profile.
 *
 * @author Jessica D'Cunha, Gaurav Sekhar
 */
public class ProfileActivity extends AppCompatActivity {
//    private FirebaseAuth mAuth;
    private UserController userController;
    private User user;
    private String role;

    private TextView fullName;
    private TextView username;
    private TextView phone;
    private TextView email;
    private TextView balance;

    private TextView ratingLabel;
    private LinearLayout ratingLayout;
    private TextView upvotes;
    private TextView downvotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        role = getIntent().getStringExtra("role");
        setTitle(getString(R.string.profile));

        userController = new UserController(this);

        fullName = findViewById(R.id.full_name);
        username = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        balance = findViewById(R.id.balance);


        userController.getUser(new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                user = (User) result.get("user");

                switchRoles(role, user);
            }
        });
    }

    public void switchRoles(String role, User user) {
        DecimalFormat decFormat = new DecimalFormat("#.00");

        switch (role) {
            case "Rider":
                fullName.setText(user.getFirstName() + " " + user.getLastName());
                username.setText(user.getUsername());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                balance.setText("$" + decFormat.format(user.getBalance()));

                break;
            case "Driver":
                ratingLabel = findViewById(R.id.ratingLabel);
                ratingLayout = findViewById(R.id.rating_layout);
                ratingLabel.setVisibility(View.VISIBLE);
                ratingLayout.setVisibility(View.VISIBLE);
                upvotes = findViewById(R.id.upvotes);
                downvotes = findViewById(R.id.downvotes);

                fullName.setText(user.getFirstName() + " " + user.getLastName());
                username.setText(user.getUsername());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                balance.setText("$" + decFormat.format(user.getBalance()));

                loadRatings(user.getUid());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // options menu contains buttons for editing contact info and logging out
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_options_menu, menu);

        if (role.equals("Driver")) {
            MenuItem menuItem = menu.findItem(R.id.top_up);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                // edit contact info
                edit();
                return true;

            case R.id.top_up:
                // modify balance
                String uid = user.getUid();
                user.addToBalance(User.START_BALANCE);
                userController.addToBalance(uid, User.START_BALANCE);

                DecimalFormat decFormat = new DecimalFormat("#.00");
                balance.setText("$" + decFormat.format(user.getBalance()));

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Your balance has been topped up!");
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                return true;

            case R.id.logout:
                // log out
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Retrieves rating information from the database.
     */
    public void loadRatings(String uid) {
        userController.getRating(uid, new Callback<Rating>() {
            @Override
            public void myResponseCallback(Rating result) {
                upvotes.setText(Integer.toString(result.getUpvotes()));
                downvotes.setText(Integer.toString(result.getDownvotes()));
            }
        });
    }

    /**
     * Handles editing of the contact info in the profile.
     */
    public void edit() {
        AlertDialog.Builder editDialogBuilder = new AlertDialog.Builder(this);
        editDialogBuilder.setTitle("Edit Contact Information");
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_dialog, null);
        editDialogBuilder.setView(dialogView);

        final EditText emailInput = (EditText) dialogView.findViewById(R.id.email_input);
        final EditText phoneInput = (EditText) dialogView.findViewById(R.id.phone_input);
        final EditText passwordInput = (EditText) dialogView.findViewById(R.id.password_input);
        emailInput.setText(email.getText());
        phoneInput.setText(phone.getText());

        editDialogBuilder.setPositiveButton("SAVE", null);

        editDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog editDialog = editDialogBuilder.create();

        editDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = editDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newEmail = emailInput.getText().toString();
                        String newPhone = phoneInput.getText().toString();
                        String password = passwordInput.getText().toString();

                        if (userController.isContactInfoValid(newEmail, newPhone, password)) {
                            userController.saveContactInfo(ProfileActivity.this, newEmail, newPhone, password);
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        editDialog.show();
    }

    public void updatePhoneEmailText(String newPhone, String newEmail) {
        phone.setText(newPhone);
        email.setText(newEmail);
    }

    /**
     * Initiates logging out of the app.
     */
    public void logout() {
        userController.logout();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(i);
    }
}
