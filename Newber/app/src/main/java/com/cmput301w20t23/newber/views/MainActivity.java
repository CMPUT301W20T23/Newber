package com.cmput301w20t23.newber.views;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;
import com.cmput301w20t23.newber.views.fragments.NoRequestFragment;
import com.cmput301w20t23.newber.views.fragments.RequestAcceptedFragment;
import com.cmput301w20t23.newber.views.fragments.RequestInProgressFragment;
import com.cmput301w20t23.newber.views.fragments.RequestOfferedFragment;
import com.cmput301w20t23.newber.views.fragments.RequestPaymentFragment;
import com.cmput301w20t23.newber.views.fragments.RequestPendingFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * The Android Activity that acts as the main user screen of the app.
 *
 * @author Amy Hou
 */
public class MainActivity extends AppCompatActivity implements Observer {
    private final UserController userController = new UserController(this);
    private final RideController rideController = new RideController();

    /**
     * The user's current ride request.
     */
    private RideRequest currRequest; // To be updated when querying db

    private String firstName, lastName, username, phone, email, uId, currentRequestId, role;
    private double balance;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("In onCreate");
    }

    public void switchRole() {
        switch (role) {
            case "Rider":
                user = new Rider(firstName, lastName, username, phone, email, uId, currentRequestId, balance);
                break;

            case "Driver":
                user = new Driver(firstName, lastName, username, phone, email, uId, currentRequestId, null, balance);
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
            this.rideController.addListenerToRideRequest(this, currentRequestId);

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
                        statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
                        riderFragment = new RequestPendingFragment(currRequest);
                    }
                    break;
                case OFFERED:
                    statusBanner.setText("Offered");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.colorOrange));
                    System.out.println(user);
                    riderFragment = new RequestOfferedFragment(currRequest, role);
                    break;
                case ACCEPTED:
                    statusBanner.setText("Accepted");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                    riderFragment = new RequestAcceptedFragment(currRequest, role);
                    break;
                case IN_PROGRESS:
                    statusBanner.setText("In Progress");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.colorYellow));
                    riderFragment = new RequestInProgressFragment(currRequest, role);
                    break;
                case PAYMENT:
                    statusBanner.setText("Payment Processing");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue));
                    riderFragment = new RequestPaymentFragment(currRequest, role);
                    break;
                case COMPLETED:
                    statusBanner.setText("Completed");
                    statusBanner.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlue));

                    //Update the User Balance, only going to be for rider side. Since, driver
                    //removes the request once it's completed
                    user.subtractFromBalance(currRequest.getCost());

                    user.setCurrentRequestId("");
                    currentRequestId = "";
                    this.userController.removeUserCurrentRequestId(user.getUid());

                    startRatingActivity();
                    riderFragment = new NoRequestFragment(role, user);
                    break;
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rider_request_details, riderFragment);
        ft.commitAllowingStateLoss();
    }

    public void startRatingActivity() {
        // Start Rating Activity
        Intent ratingIntent = new Intent(this, RatingActivity.class);
        ratingIntent.putExtra("driverUid", currRequest.getDriver());

        // Set request to null since there is no need for it anymore
        currRequest = null;
        startActivity(ratingIntent);
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

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("In Update: ");
        if (arg == null) {
            currRequest = null;
        } else {
            currRequest = (RideRequest) arg;
            System.out.println("In notified observer: " + currRequest.toString() + " " + currRequest.getStatus());
            System.out.println("driver: " + currRequest.getDriver());
            if ((currRequest.getDriver() == null || currRequest.getDriver() == "" ) && role.matches("Driver")) {
                System.out.println("setting curr to null");
                currRequest = null;
            }
        }

        displayFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                String scannedRequestId = result.getContents();

                if (scannedRequestId.equals(currRequest.getRequestId())) {
                    this.userController.transferBalance(currRequest);
                    this.rideController.finishRideRequest(user, currRequest);
                    currRequest = null;
                }
            }
        }

        displayFragment();
    }

    @Override
    protected void onStop() {
        this.rideController.removeListeners(this, currentRequestId);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("In onResume");

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
                balance = responseUser.getBalance();

                role = (String) result.get("role");
                switchRole();
                displayFragments();
            }
        });
    }
}
