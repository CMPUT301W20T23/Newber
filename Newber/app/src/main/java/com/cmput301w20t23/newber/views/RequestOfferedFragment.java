package com.cmput301w20t23.newber.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.NameOnClickListener;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * The Android Fragment that is shown when the user has an offered current ride request.
 *
 * @author Amy Hou
 */
public class RequestOfferedFragment extends Fragment {

    private RideRequest rideRequest;
    private String role;

    /**
     * Instantiate RideRequest controller
     */
    private RideController rideController = new RideController();
    private UserController userController = new UserController(this.getContext());

    /**
     * Instantiates a new RequestOfferedFragment.
     *
     * @param request the current request
     * @param role    the user's role
     */
    public RequestOfferedFragment(RideRequest request, String role) {
        this.rideRequest = request;
        this.role = role;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // View for this fragment
        View view = inflater.inflate(R.layout.offered_fragment, container, false);

        // Get view elements
        TextView pickupLocationTextView = view.findViewById(R.id.pickup_location);
        TextView dropoffLocationTextView = view.findViewById(R.id.dropoff_location);
        TextView fareTextView = view.findViewById(R.id.ride_fare);
        TextView userLabelTextView = view.findViewById(R.id.user_label);
        TextView usernameTextView = view.findViewById(R.id.username);
        Button acceptOfferButton = view.findViewById(R.id.rider_accept_offer_button);
        Button declineOfferButton = view.findViewById(R.id.rider_decline_offer_button);

        // Set view elements
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(Double.toString(rideRequest.getCost()));

        switch (role) {
            case "Rider":
                userLabelTextView.setText("Driver:");
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                View dialogView = inflater.inflate(R.layout.profile_dialog, null);
                dialogBuilder.setView(dialogView);

                // Set values of info box
                ((MainActivity) getActivity()).userController.getUser(rideRequest.getDriver(),
                        new Callback<Map<String, Object>>() {
                            @Override
                            public void myResponseCallback(Map<String, Object> result) {
                                User driver = (User) result.get("user");
                                nameTextView.setText(driver.getUsername());
                                phoneTextView.setText(driver.getPhone());
                                emailTextView.setText(driver.getEmail());
                                nameTextView.setOnClickListener(new NameOnClickListener(role, driver));
                                nameTextView.setOnClickListener(new NameOnClickListener(role, driver));
                            }
                        });

//                nameTextView.setText(rideRequest.getDriver().getUsername());
//                phoneTextView.setText(rideRequest.getDriver().getPhone());
//                emailTextView.setText(rideRequest.getDriver().getEmail());

                // Rider can click Accept or Decline to the driver's offer
                acceptOfferButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // TODO: Leave driver attached to request on firebase and set request status to ACCEPTED
                        rideRequest.setStatus(RequestStatus.ACCEPTED);
                        rideController.updateRideRequest(rideRequest);
                    }
                });

                declineOfferButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // TODO: Request status returns to PENDING and remove driver from request on Firebase
                        rideRequest.setStatus(RequestStatus.PENDING);
//                        rideRequest.getDriver().setCurrentRequestId("");
                        userController.removeUserCurrentRequestId(rideRequest.getDriver());
                        rideRequest.setDriver(null);
                        rideController.updateRideRequest(rideRequest);
                    }
                });

                // Bring up profile when name is clicked
//                nameTextView.setOnClickListener(new NameOnClickListener(role, rideRequest.getDriver()));
                break;

            case "Driver":
                // Set values of info box
                ((MainActivity) getActivity()).userController.getUser(rideRequest.getRider(),
                        new Callback<Map<String, Object>>() {
                            @Override
                            public void myResponseCallback(Map<String, Object> result) {
                                User rider = (User) result.get("user");
                                nameTextView.setText(rider.getUsername());
                                phoneTextView.setText(rider.getPhone());
                                emailTextView.setText(rider.getEmail());
                                nameTextView.setOnClickListener(new NameOnClickListener(role, rider));
                            }
                        });

                // Set values of info box
//                nameTextView.setText(rideRequest.getRider().getUsername());
//                phoneTextView.setText(rideRequest.getRider().getPhone());
//                emailTextView.setText(rideRequest.getRider().getEmail());

                // Show decline/accept offer buttons only for Riders
                acceptOfferButton.setVisibility(View.INVISIBLE);
                declineOfferButton.setVisibility(View.INVISIBLE);

                // Bring up profile when name is clicked
//                nameTextView.setOnClickListener(new NameOnClickListener(role, rideRequest.getRider()));
                break;
        }

        return view;
    }
}
