package com.cmput301w20t23.newber.views.fragments;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.NameOnClickListener;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.User;

import java.util.Locale;
import java.util.Map;
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
    private final RideController rideController = new RideController();
    private final UserController userController = new UserController(this.getContext());

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
        final TextView usernameTextView = view.findViewById(R.id.username);
        Button acceptOfferButton = view.findViewById(R.id.rider_accept_offer_button);
        Button declineOfferButton = view.findViewById(R.id.rider_decline_offer_button);

        // Set view elements
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(String.format(Locale.US, "$%.2f", rideRequest.getCost()));

        switch (role) {
            case "Rider":
                userLabelTextView.setText("Driver: ");

                // Set values of info box
                userController.getUser(rideRequest.getDriver(), new Callback<Map<String, Object>>() {
                    @Override
                    public void myResponseCallback(Map<String, Object> result) {
                        User driver = (User) result.get("user");
                        usernameTextView.setText(driver.getUsername());
                        usernameTextView.setPaintFlags(usernameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        usernameTextView.setOnClickListener(new NameOnClickListener(getActivity(), userController, role, driver));
                    }
                });

                // Rider can click Accept or Decline to the driver's offer
                acceptOfferButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        rideRequest.setStatus(RequestStatus.ACCEPTED);
                        rideController.updateRideRequest(rideRequest);
                    }
                });

                declineOfferButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        rideRequest.setStatus(RequestStatus.PENDING);
                        userController.removeUserCurrentRequestId(rideRequest.getDriver());
                        rideRequest.setDriver(null);
                        rideController.updateRideRequest(rideRequest);
                    }
                });

                break;

            case "Driver":
                userLabelTextView.setText("Rider: ");

                // Set values of info box
                userController.getUser(rideRequest.getRider(), new Callback<Map<String, Object>>() {
                    @Override
                    public void myResponseCallback(Map<String, Object> result) {
                        User rider = (User) result.get("user");
                        usernameTextView.setText(rider.getUsername());
                        usernameTextView.setPaintFlags(usernameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        usernameTextView.setOnClickListener(new NameOnClickListener(getActivity(), userController, role, rider));
                    }
                });

                // Show decline/accept offer buttons only for Riders
                acceptOfferButton.setVisibility(View.INVISIBLE);
                declineOfferButton.setVisibility(View.INVISIBLE);

                break;
        }

        return view;
    }
}
