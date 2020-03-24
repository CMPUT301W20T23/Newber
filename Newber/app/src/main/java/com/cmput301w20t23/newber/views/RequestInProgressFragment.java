package com.cmput301w20t23.newber.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.NameOnClickListener;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Map;

/**
 * The Android Fragment that is shown when the user has an in-progress current ride request.
 *
 * @author Amy Hou
 */
public class RequestInProgressFragment extends Fragment {

    private RideRequest rideRequest;
    private String role;

    /**
     * Instantiate RideRequest controller
     */
    private RideController rideController = new RideController();

    /**
     * Instantiates a new RequestInProgressFragment.
     *
     * @param request the current request
     * @param role    the user's role
     */
    public RequestInProgressFragment(RideRequest request, String role) {
        this.rideRequest = request;
        this.role = role;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // View for this fragment
        View view = inflater.inflate(R.layout.in_progress_fragment, container, false);

        // Get view elements
        TextView pickupLocationTextView = view.findViewById(R.id.pickup_location);
        TextView dropoffLocationTextView = view.findViewById(R.id.dropoff_location);
        TextView fareTextView = view.findViewById(R.id.ride_fare);
        final TextView nameTextView = view.findViewById(R.id.rider_main_driver_name);
        final TextView phoneTextView = view.findViewById(R.id.rider_main_driver_phone);
        final TextView emailTextView = view.findViewById(R.id.rider_main_driver_email);
        Button completeButton = view.findViewById(R.id.driver_complete_ride_button);

        // Set view elements
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(Double.toString(rideRequest.getCost()));

        switch (role)
        {
            case "Rider":
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
                            }
                        });

//                nameTextView.setText(rideRequest.getDriver().getUsername());
//                phoneTextView.setText(rideRequest.getDriver().getPhone());
//                emailTextView.setText(rideRequest.getDriver().getEmail());

                completeButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bannerBlue));

                completeButton.setText("Complete");

                completeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // TODO: Set request status to COMPLETED and move Rider to PAYMENT screen

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle("Complete Ride");
                        dialogBuilder.setMessage("Are you sure this ride has been completed?");

                        dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                rideRequest.setStatus(RequestStatus.COMPLETED);
                                rideController.updateRideRequest(rideRequest);

                                // TODO: Start RiderPaymentActivity
                            }
                        });

                        dialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
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

//                nameTextView.setText(rideRequest.getRider().getUsername());
//                phoneTextView.setText(rideRequest.getRider().getPhone());
//                emailTextView.setText(rideRequest.getRider().getEmail());

                // Complete ride button only visible by rider; driver hides it
                completeButton.setVisibility(View.INVISIBLE);

                // Bring up profile when name is clicked
//                nameTextView.setOnClickListener(new NameOnClickListener(role, rideRequest.getRider()));
                break;
        }

        return view;
    }
}
