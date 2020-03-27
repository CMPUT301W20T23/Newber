package com.cmput301w20t23.newber.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.Locale;
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
    private UserController userController = new UserController(this.getContext());
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
        TextView userLabelTextView = view.findViewById(R.id.user_label);
        final TextView usernameTextView = view.findViewById(R.id.username);
        Button completeButton = view.findViewById(R.id.driver_complete_ride_button);

        // Set view elements
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(String.format(Locale.US, "$%.2f", rideRequest.getCost()));

        switch (role)
        {
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

                // Complete ride button only visible by driver; rider hides it
                completeButton.setVisibility(View.INVISIBLE);

                break;

            case "Driver":
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

                completeButton.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bannerBlue));

                completeButton.setText("Complete");

                completeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
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

                break;
        }

        return view;
    }
}
