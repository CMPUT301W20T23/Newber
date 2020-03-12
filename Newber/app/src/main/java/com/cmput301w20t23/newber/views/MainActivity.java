package com.cmput301w20t23.newber.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The Android Activity that acts as the main user screen of the app.
 *
 * @author Amy Hou
 */
public class MainActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

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

        // Get current userID
        String userId = mAuth.getCurrentUser().getUid();

        System.out.println(userId);

        // Get User object using Firebase users table
        database.getReference("users")
                .child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firstName = dataSnapshot.child("firstName").getValue(String.class);
                lastName = dataSnapshot.child("lastName").getValue(String.class);
                username = dataSnapshot.child("username").getValue(String.class);
                phone = dataSnapshot.child("phone").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);
                uId = mAuth.getCurrentUser().getUid();
                currentRequestId = dataSnapshot.child("currentRequestId").getValue(String.class);
                role = dataSnapshot.child("role").getValue(String.class);
                System.out.println("changed");

                switch (role) {
                    case "Rider":
                        user = new Rider(firstName, lastName, username, phone, email, uId, currentRequestId);
                        break;

                    case "Driver":
                        database.getReference("drivers").child(uId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Rating rating = dataSnapshot.getValue(Rating.class);
                                user = new Driver(firstName, lastName, username, phone, email, uId, currentRequestId, rating);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        break;
                }

                // Use User.currRequestId to get RideRequest object from requests table
                if (currentRequestId != null && !currentRequestId.isEmpty()) {
                    System.out.println("currReqId not null");

                    database.getReference("rideRequests")
                            .child(currentRequestId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                currRequest = dataSnapshot.getValue(RideRequest.class);
//                                updateUsers();
                                displayFragment();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    currRequest = null;
                    displayFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateUsers() {
        switch (role) {
            case "Rider":
                // Get information of driver associated with current request
                System.out.println("driver uid is: " + currRequest.getDriverUid());
                if (currRequest.getDriverUid() != null && !currRequest.getDriverUid().isEmpty()) {
                    System.out.println("I'm in");
                    database.getReference("users").child(currRequest.getDriverUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            firstName = dataSnapshot.child("firstName").getValue(String.class);
                            lastName = dataSnapshot.child("lastName").getValue(String.class);
                            username = dataSnapshot.child("username").getValue(String.class);
                            phone = dataSnapshot.child("phone").getValue(String.class);
                            email = dataSnapshot.child("email").getValue(String.class);
                            uId = currRequest.getDriverUid();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    database.getReference("drivers").child(currRequest.getDriverUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Rating rating = dataSnapshot.getValue(Rating.class);
                            System.out.println("before driver make, username is: " + username);
                            driver = new Driver(firstName, lastName, username, phone, email, uId, currRequest.getRequestId(), rating);
                            System.out.println("driver added from rider");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                break;

            case "Driver":
                // Get rider associated with current request
                System.out.println("in the rider");
                database.getReference("users").child(currRequest.getRiderUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("In datachange");
                        firstName = dataSnapshot.child("firstName").getValue(String.class);
                        lastName = dataSnapshot.child("lastName").getValue(String.class);
                        username = dataSnapshot.child("username").getValue(String.class);
                        phone = dataSnapshot.child("phone").getValue(String.class);
                        email = dataSnapshot.child("email").getValue(String.class);
                        uId = currRequest.getRiderUid();
                        rider = new Rider(firstName, lastName, username, phone, email, uId, currRequest.getRequestId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
        }
    }

    public void displayFragment() {
        Fragment riderFragment = null;
        TextView statusBanner = findViewById(R.id.main_status_banner);

        System.out.println("the role is: " + role);

        if (currRequest == null) {
            // if current user has no request attached, use "no current request" fragment
            statusBanner.setText("No Request");
            statusBanner.setBackgroundColor(Color.LTGRAY);
            riderFragment = new NoRequestFragment(role, user);
        }
        else {
            switch (currRequest.getStatus()) {
                case PENDING:
                    statusBanner.setText("Requested");
                    statusBanner.setBackgroundColor(Color.RED);
                    riderFragment = new RequestPendingFragment(currRequest);
                    break;
                case OFFERED:
                    statusBanner.setText("Offered");
                    statusBanner.setBackgroundColor(Color.rgb(255,165,0)); // orange
                    System.out.println(user);
                    riderFragment = new RequestOfferedFragment(currRequest, role);
                    break;
                case ACCEPTED:
                    statusBanner.setText("Accepted");
                    statusBanner.setBackgroundColor(Color.GREEN);
                    riderFragment = new RequestAcceptedFragment(currRequest, role);
                    break;
                case IN_PROGRESS:
                    statusBanner.setText("In Progress");
                    statusBanner.setBackgroundColor(Color.YELLOW);
                    riderFragment = new RequestInProgressFragment(currRequest, role);
                    break;
                case COMPLETED:
                    statusBanner.setText("Completed");
                    statusBanner.setBackgroundColor(Color.CYAN);
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
