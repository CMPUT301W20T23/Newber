package com.cmput301w20t23.newber.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.models.User;

/**
 * This class listens for a user's name to be clicked and brings up the user profile
 *
 * @author Amy Hou
 */
public class NameOnClickListener implements View.OnClickListener {
    /**
     * The Role.
     */
    private UserController userController;
    private Context context;
    private String role;
    private User user;

    /**
     * Instantiates a new NameOnClickListener.
     *
     * @param role      the user's role
     * @param user      the user whose profile we will show
     */
    public NameOnClickListener(Context context, UserController userController, String role, User user) {
        this.context = context;
        this.userController = userController;
        this.role = role;
        this.user = user;
    }
    @Override
    public void onClick(View view) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.activity_profile, null);

        // ensure dialog content has margins
        FrameLayout dialogLayout = new FrameLayout(context);
        dialogLayout.setPadding(50, 50, 50, 50);

        dialogLayout.addView(dialogView);
        dialogBuilder.setView(dialogLayout);

        TextView ratingLabel = dialogView.findViewById(R.id.ratingLabel);
        LinearLayout ratingLayout = dialogView.findViewById(R.id.rating_layout);
        TextView fullName = dialogView.findViewById(R.id.full_name);
        TextView username = dialogView.findViewById(R.id.username);
        TextView phone = dialogView.findViewById(R.id.phone);
        TextView email = dialogView.findViewById(R.id.email);

        fullName.setText(user.getFirstName() + " " + user.getLastName());
        username.setText(user.getUsername());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());

        if (role.equals("Rider")) {
            // Get driver rating info
            ratingLabel.setVisibility(View.VISIBLE);
            ratingLayout.setVisibility(View.VISIBLE);

            final TextView upvotes = dialogView.findViewById(R.id.upvotes);
            final TextView downvotes = dialogView.findViewById(R.id.downvotes);

            userController.getRating(user.getUid(), new Callback<Rating>() {
                @Override
                public void myResponseCallback(Rating result) {
                    upvotes.setText(Integer.toString(result.getUpvotes()));
                    downvotes.setText(Integer.toString(result.getDownvotes()));
                }
            });
        }

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
