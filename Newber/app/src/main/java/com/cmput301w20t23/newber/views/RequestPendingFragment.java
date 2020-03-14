package com.cmput301w20t23.newber.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.models.RideRequest;
import com.cmput301w20t23.newber.models.Rider;

/**
 * The Android Fragment that is shown when the user has a pending current ride request.
 *
 * @author Amy Hou
 */
public class RequestPendingFragment extends Fragment {

    private RideRequest rideRequest;

    /**
     * Instantiate User and RideRequest controllers
     */
    private RideController rideController = new RideController();
    private UserController userController = new UserController(this.getContext());

    /**
     * Instantiates a new RequestPendingFragment.
     *
     * @param request the current request
     */
    public RequestPendingFragment(RideRequest request) {
        this.rideRequest = request;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater of layout for this fragment
        View view = inflater.inflate(R.layout.pending_fragment, container, false);

        // Get view elements
        TextView pickupLocationTextView = view.findViewById(R.id.pickup_location);
        TextView dropoffLocationTextView = view.findViewById(R.id.dropoff_location);
        TextView fareTextView = view.findViewById(R.id.ride_fare);
        Button cancelRequestButton = view.findViewById(R.id.rider_pending_request_button);

        // Set view elements
        System.out.println(rideRequest.getStartLocation());
        pickupLocationTextView.setText(rideRequest.getStartLocation().getName());
        dropoffLocationTextView.setText(rideRequest.getEndLocation().getName());
        fareTextView.setText(Double.toString(rideRequest.getCost()));

        cancelRequestButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Remove current request ID from firebase user entry
//                rideRequest.getRider().setCurrentRequestId("");
                userController.removeUserCurrentRequestId(rideRequest.getRider());

                // Remove ride request entry from firebase
                rideController.removeRideRequest(rideRequest);
            }
        });
        return view;
    }
}