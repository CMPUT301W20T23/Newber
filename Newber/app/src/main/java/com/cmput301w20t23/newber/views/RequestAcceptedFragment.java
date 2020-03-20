package com.cmput301w20t23.newber.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.NameOnClickListener;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
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
 * The Android Fragment that is shown when the user has an accepted current ride request.
 *
 * @author Amy Hou
 */
public class RequestAcceptedFragment extends Fragment {

    private RideRequest rideRequest;
    private String role;
    private User driver;
    private User rider;

    /**
     * Instantiate User and RideRequest controllers
     */
    private RideController rideController = new RideController();
    private UserController userController = new UserController(this.getContext());

    /**
     * Instantiates a new RequestAcceptedFragment.
     *
     * @param request the user's current request
     * @param role    the user's role
     */
    public RequestAcceptedFragment(RideRequest request, String role) {
        this.rideRequest = request;
        this.role = role;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // View for this fragment
        final View view = inflater.inflate(R.layout.accepted_fragment, container, false);

        // Get view elements
        TextView pickupLocationTextView = view.findViewById(R.id.pickup_location);
        TextView dropoffLocationTextView = view.findViewById(R.id.dropoff_location);
        TextView fareTextView = view.findViewById(R.id.ride_fare);
        final TextView nameTextView = view.findViewById(R.id.rider_main_driver_name);
        final TextView phoneTextView = view.findViewById(R.id.rider_main_driver_phone);
        final TextView emailTextView = view.findViewById(R.id.rider_main_driver_email);
        Button button = view.findViewById(R.id.request_accepted_button);

        // Set view elements
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(Double.toString(rideRequest.getCost()));

        // Change UI based on role
        switch(role)
        {
            case "Rider": // Cancel button
                button.setBackgroundColor(Color.LTGRAY);
                button.setText("Cancel");

                ((MainActivity) getActivity()).userController.getUser(rideRequest.getDriver(),
                        new Callback<Map<String, Object>>() {
                            @Override
                            public void myResponseCallback(Map<String, Object> result) {
                                driver = (User) result.get("user");
                                nameTextView.setText(driver.getUsername());
                                phoneTextView.setText(driver.getPhone());
                                emailTextView.setText(driver.getEmail());
                            }
                        });

//                // Set values of info box
//                nameTextView.setText(rideRequest.getDriver().getUsername());
//                phoneTextView.setText(rideRequest.getDriver().getPhone());
//                emailTextView.setText(rideRequest.getDriver().getEmail());

                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // If rider, remove driver from request and set status to PENDING
//                        rideRequest.getDriver().setCurrentRequestId("");

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle("Cancel Ride");
                        dialogBuilder.setMessage("Are you sure you want to cancel this ride? Your request will remain for other drivers to browse.");

                        dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                userController.removeUserCurrentRequestId(rideRequest.getDriver());
                                rideRequest.setDriver(null);
                                rideRequest.setStatus(RequestStatus.PENDING);
                                rideController.updateRideRequest(rideRequest);

                                setUpButtons(view);
                                setUpNameTextView(nameTextView, driver);
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

//                ImageButton callButton = view.findViewById(R.id.call_button);
//                ImageButton emailButton = view.findViewById(R.id.email_button);
//                callButton.setVisibility(View.VISIBLE);
//                emailButton.setVisibility(View.VISIBLE);
//
//                callButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        goToCallScreen(rideRequest.getDriver());
//                    }
//                });
//
//                emailButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        goToMailScreen(rideRequest.getDriver());
//                    }
//                });

                // Bring up profile when name is clicked
//                nameTextView.setOnClickListener(new NameOnClickListener(role, rideRequest.getDriver()));

                break;

            case "Driver": // Rider Picked Up button
                button.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bannerYellow));
                button.setText("Rider picked up");

                // Set values of info box
                ((MainActivity) getActivity()).userController.getUser(rideRequest.getDriver(),
                        new Callback<Map<String, Object>>() {
                            @Override
                            public void myResponseCallback(Map<String, Object> result) {
                                rider = (User) result.get("user");
                                nameTextView.setText(rider.getUsername());
                                phoneTextView.setText(rider.getPhone());
                                emailTextView.setText(rider.getEmail());
                                setUpNameTextView(nameTextView, rider);
                            }
                        });

//                nameTextView.setText(rideRequest.getRider().getUsername());
//                phoneTextView.setText(rideRequest.getRider().getPhone());
//                emailTextView.setText(rideRequest.getRider().getEmail());

                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // TODO: If driver, set request status to IN_PROGRESS
                        rideRequest.setStatus(RequestStatus.IN_PROGRESS);
                        rideController.updateRideRequest(rideRequest);
                    }
                });

                // Bring up profile when name is clicked
//                nameTextView.setOnClickListener(new NameOnClickListener(role, rideRequest.getRider()));
                break;
        }

        return view;
    }

    public void setUpButtons(View view) {
        ImageButton callButton = view.findViewById(R.id.call_button);
        ImageButton emailButton = view.findViewById(R.id.email_button);
        callButton.setVisibility(View.VISIBLE);
        emailButton.setVisibility(View.VISIBLE);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCallScreen(driver);
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMailScreen(driver);
            }
        });
    }

    public void setUpNameTextView(TextView nameTextView, User user) {
        nameTextView.setOnClickListener(new NameOnClickListener(role, user));
    }

    /**
     * Opens Android call screen and populates it with the driver's phone number when the
     * appropriate button is clicked.
     */
    public void goToCallScreen(User user) {
        // TODO: replace dummy phone with driver's phone
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getPhone()));
        this.startActivity(callIntent);
    }

    /**
     * Opens Android mail screen and populates it with the driver's email address when the
     * appropriate button is clicked.
     */
    public void goToMailScreen(User user) {
        // TODO: replace dummy email with driver's email
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {user.getEmail()});
        mailIntent.setType("message/rfc822");
        this.startActivity(Intent.createChooser(mailIntent,
                "Send email using: "));
    }
}
