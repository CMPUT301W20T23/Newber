package com.cmput301w20t23.newber.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.models.Driver;
import com.cmput301w20t23.newber.models.Rider;
import com.cmput301w20t23.newber.models.User;

/**
 * The Android Fragment that is shown when the user doesn't have a current ride request.
 *
 * @author Amy Hou
 */
public class NoRequestFragment extends Fragment {
    private String role;
    private User user;

    /**
     * Instantiates a new NoRequestFragment.
     *
     * @param role the user's role
     */
    public NoRequestFragment(String role, User user) {
        this.role = role;
        this.user = user;
    }

    public NoRequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflater of layout for this fragment
        View view = inflater.inflate(R.layout.no_request_fragment, container, false);

        Button createRequestButton = view.findViewById(R.id.create_request_button);

        switch (role) {
            case "Rider":
                createRequestButton.setText("Make a request");
                createRequestButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getActivity(), RiderRequestActivity.class);
                        intent.putExtra("rider", (Rider)user);
                        startActivity(intent);
                    }
                });
                break;
            case "Driver":
                createRequestButton.setText("Search for a ride");
                createRequestButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(getActivity(), DriverRequestActivity.class);
                        intent.putExtra("driver", (Driver)user);
                        startActivity(intent);
                    }
                });
                break;
        }

        return view;
    }
}