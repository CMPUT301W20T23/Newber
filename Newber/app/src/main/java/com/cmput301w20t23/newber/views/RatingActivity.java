package com.cmput301w20t23.newber.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.cmput301w20t23.newber.R;
import com.cmput301w20t23.newber.controllers.RideController;
import com.cmput301w20t23.newber.controllers.UserController;
import com.cmput301w20t23.newber.models.Rating;
import com.cmput301w20t23.newber.helpers.Callback;
import com.cmput301w20t23.newber.models.RideRequest;

import java.util.Map;

public class RatingActivity extends AppCompatActivity {

    private UserController userController;
    private RideController rideController;
    private String driverUid;
    private Rating rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        setTitle("Rate Your Driver");

        userController = new UserController(this);
        rideController = new RideController();

        ImageButton goodRatingButton = findViewById(R.id.good_rating_button);
        ImageButton badRatingButton = findViewById(R.id.bad_rating_button);
        Button skipRatingButton = findViewById(R.id.skip_rating_button);

        userController.getUser(new Callback<Map<String, Object>>() {
            @Override
            public void myResponseCallback(Map<String, Object> result) {
                String requestId = (String) result.get("requestId");
                rideController.getRideRequest(requestId, new Callback<RideRequest>() {
                    @Override
                    public void myResponseCallback(RideRequest result) {
                        RideRequest tempRequest = result;
                        final String driverUid = tempRequest.getDriver();

                        userController.getRating(driverUid, new Callback<Rating>() {
                            @Override
                            public void myResponseCallback(Rating rating) {
                                switchRating(driverUid, rating);
                            }
                        });
                    }
                });
            }
        });

//        // Hardcoded test
//        final String driverUid = "9lhxPOJMDzZVmmLnkSuAX2PHcBU2";
//
//        userController.getRating(driverUid, new Callback<Rating>() {
//            @Override
//            public void myResponseCallback(Rating rating) {
//                switchRating(driverUid, rating);
//            }
//        });

        goodRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("good rating received");
                // Increase driver's upvotes by 1
                rating.upvote();
                userController.updateRating(driverUid, rating);

                // Finish activity
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

            }
        });

        skipRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to main screen
                // Finish activity
            }
        });
    }

    public void switchRating(String driverUid, Rating rating) {
        this.driverUid = driverUid;
        this.rating = rating;
    }
}
