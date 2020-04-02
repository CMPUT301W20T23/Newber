package com.cmput301w20t23.newber.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.Rating;

public class RatingActivity extends AppCompatActivity {

    private UserController userController;
    private String driverUid;
    private Rating rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        setTitle("Rate Your Driver");

        userController = new UserController(this);

        // Get Driver UID through Intent
        driverUid = getIntent().getStringExtra("driverUid");
        userController.getRating(driverUid, new Callback<Rating>() {
            @Override
            public void myResponseCallback(Rating rating) {
                RatingActivity.this.rating = rating;
            }
        });

        ImageButton goodRatingButton = findViewById(R.id.good_rating_button);
        ImageButton badRatingButton = findViewById(R.id.bad_rating_button);
        Button skipRatingButton = findViewById(R.id.skip_rating_button);

        goodRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("good rating received");
                // Increase driver's upvotes by 1
                rating.upvote();
                userController.updateRating(driverUid, rating);

                // Finish activity
                finish();
            }
        });

        badRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("bad rating received");
                // Decrease driver's upvotes by 1
                rating.downvote();
                userController.updateRating(driverUid, rating);

                // Finish activity
                finish();
            }
        });

        skipRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to main screen
                // Finish activity
                finish();
            }
        });
    }

    public void switchRating(String driverUid, Rating rating) {
        this.driverUid = driverUid;
        this.rating = rating;
    }
}
