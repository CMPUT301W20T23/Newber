package com.cmput301w20t23.newber.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;

import java.util.Map;

/**
 * The Android Activity that acts as the main user screen of the app.
 *
 * @author Amy Hou
 */
public class MainActivity extends AppCompatActivity {
    private final UserController userController = new UserController(this);
    private final RideController rideController = new RideController();

    /**
     * The user's current ride request.
     */
    private RideRequest currRequest; // To be updated when querying db

    private String firstName, lastName, username, phone, email, uId, currentRequestId, role;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get User object using User Controller
        this.userController.getUser(new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                User responseUser = (User) result.get("user");
                firstName = responseUser.getFirstName();
                lastName = responseUser.getLastName();
                username = responseUser.getUsername();
                phone = responseUser.getPhone();
                email = responseUser.getEmail();
                uId = responseUser.getUid();
                currentRequestId = responseUser.getCurrentRequestId();
                role = (String) result.get("role");
                switchRole();
                displayFragments();
            }
        });

    }

    public void switchRole() {
        switch (role) {
            case "Rider":
                user = new Rider(firstName, lastName, username, phone, email, uId, currentRequestId);
                break;

            case "Driver":
                user = new Driver(firstName, lastName, username, phone, email, uId, currentRequestId, null);
                this.userController.getRating(uId, new Callback<Rating>() {
                    @Override
                    public void myResponseCallback(Rating result) {
                        Rating rating = result;
                    }
                });
                break;
        }
    }

    public void displayFragments() {
        if (currentRequestId != null && !currentRequestId.isEmpty()) {
            this.rideController.getRideRequest(currentRequestId, new Callback<RideRequest>() {
                @Override
                public void myResponseCallback(RideRequest result) {
                    currRequest = result;
                    displayFragment();
                }
            });
        } else {
            currRequest = null;
            displayFragment();
        }
    }

    public void displayFragment() {
        Fragment riderFragment = null;
        TextView statusBanner = findViewById(R.id.main_status_banner);

        System.out.println("the role is: " + role);
        System.out.println(user.toString());

        if (currRequest == null) {
            // if current user has no request attached, use "no current request" fragment
            statusBanner.setText("No Request");
            statusBanner.setBackgroundColor(Color.LTGRAY);
            riderFragment = new NoRequestFragment(role, user);
        }
        else {
            switch (currRequest.getStatus()) {
                case PENDING:
                    if (role.matches("Rider")) {
                        statusBanner.setText("Requested");
                        statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.bannerRed));
                        riderFragment = new RequestPendingFragment(currRequest);
                    } else {
                        riderFragment = new NoRequestFragment(role, user);
                    }
                    break;
                case OFFERED:
                    statusBanner.setText("Offered");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.bannerOrange));
                    System.out.println(user);
                    riderFragment = new RequestOfferedFragment(currRequest, role);
                    break;
                case ACCEPTED:
                    statusBanner.setText("Accepted");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.bannerGreen));
                    riderFragment = new RequestAcceptedFragment(currRequest, role);
                    break;
                case IN_PROGRESS:
                    statusBanner.setText("In Progress");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.bannerYellow));
                    riderFragment = new RequestInProgressFragment(currRequest, role);
                    break;
                case COMPLETED:
                    statusBanner.setText("Completed");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.bannerBlue));
                    riderFragment = new RequestCompletedFragment(currRequest);
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rider_request_details, riderFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // options menu contains button going to profile
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                // go to profile activity
                Intent i = new Intent(this, ProfileActivity.class);
                this.startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
