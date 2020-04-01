package com.cmput301w20t23.newber.views.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.NameOnClickListener;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.RequestStatus;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.User;
import com.cmput301w20t23.newber.views.MainActivity;

import java.util.Locale;
import java.util.Map;

/**
 * The Android Fragment that is shown when the user has an accepted current ride request.
 *
 * @author Amy Hou
 */
public class RequestAcceptedFragment extends Fragment {

    private RideRequest rideRequest;
    private String role;

    /**
     * Instantiate User and RideRequest controllers
     */
    private final RideController rideController = new RideController();
    private final UserController userController = new UserController(this.getContext());

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
        TextView userLabelTextView = view.findViewById(R.id.user_label);
        final TextView usernameTextView = view.findViewById(R.id.username);
        Button button = view.findViewById(R.id.request_accepted_button);

        // Set view elements
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(String.format(Locale.US, "$%.2f", rideRequest.getCost()));

        // Change UI based on role
        switch(role)
        {
            case "Rider": // Cancel button
                button.setBackgroundColor(Color.LTGRAY);
                button.setText("Cancel");

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

                setUpContactButtons(view);

                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // If rider, remove driver from request and set status to PENDING
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

                break;

            case "Driver": // Rider Picked Up button
                button.setText("Rider picked up");

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

                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        rideRequest.setStatus(RequestStatus.IN_PROGRESS);
                        rideController.updateRideRequest(rideRequest);
                    }
                });

                break;
        }
        return view;
    }

    public void setUpContactButtons(View view) {
        Button callButton = view.findViewById(R.id.call_button);
        Button emailButton = view.findViewById(R.id.email_button);
        callButton.setVisibility(View.VISIBLE);
        emailButton.setVisibility(View.VISIBLE);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCallScreen(rideRequest.getDriver());
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMailScreen(rideRequest.getDriver());
            }
        });
    }

    /**
     * Opens Android call screen and populates it with the driver's phone number when the
     * appropriate button is clicked.
     */
    public void goToCallScreen(String driverId) {
        userController.getUser(driverId, new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                User driver = (User) result.get("user");
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + driver.getPhone()));
                getActivity().startActivity(callIntent);
            }
        });
    }

    /**
     * Opens Android mail screen and populates it with the driver's email address when the
     * appropriate button is clicked.
     */
    public void goToMailScreen(String driverId) {
        userController.getUser(driverId, new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                User driver = (User) result.get("user");
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {driver.getEmail()});
                mailIntent.setType("message/rfc822");
                getActivity().startActivity(Intent.createChooser(mailIntent,
                        "Send email using: "));
            }
        });
    }
}
