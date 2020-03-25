package com.cmput301w20t23.newber;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301w20t23.newber.views.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class RequestFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    public void testRequestFragments(){
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);

        //login as rider
        solo.enterText((EditText) solo.getView(R.id.email_login), "testRider@rider.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "password");
        solo.clickOnView(solo.getView(R.id.login_button));
        solo.clickOnView((solo.getView(R.id.create_request_button)));
        //need to sleep so map loads
        solo.sleep(2500);

        //search
        solo.clickOnView(solo.getView(R.id.from_map_button));
        solo.clickLongOnView(solo.getView(R.id.map));
        solo.clickOnView(solo.getView(R.id.to_map_button));
        solo.clickLongOnView(solo.getView(R.id.map));
        solo.clickOnView(solo.getView(R.id.confirm_ride_request_button));


        //check request was made
        TextView text;
        text = (TextView) solo.getView(R.id.pickup_location);
        assertEquals("8067 104 Ave NW, Edmonton, AB T5J 4X1, Canada", text.getText());
        text = (TextView) solo.getView(R.id.dropoff_location);
        assertEquals("8067 104 Ave NW, Edmonton, AB T5J 4X1, Canada", text.getText());

        //logout of rider and into driver
        solo.clickOnView(solo.getView(R.id.profile));
        solo.clickOnView(solo.getView(R.id.logout));
        solo.sleep(1000);
        solo.enterText((EditText) solo.getView(R.id.email_login), "testDriver@driver.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "password");
        solo.clickOnView(solo.getView(R.id.login_button));
        solo.clickOnView(solo.getView(R.id.create_request_button));

        //accept ride
        solo.sleep(2500);
        solo.clickOnView(solo.getView(R.id.driver_map_button));
        solo.clickLongOnView(solo.getView(R.id.map));
        solo.clickInList(0);
        solo.clickOnView(solo.getView(R.id.confirm_ride_request_button));

        //check driver found request
        text = (TextView) solo.getView(R.id.pickup_location);
        assertEquals("8067 104 Ave NW, Edmonton, AB T5J 4X1, Canada", text.getText());
        text = (TextView) solo.getView(R.id.dropoff_location);
        assertEquals("8067 104 Ave NW, Edmonton, AB T5J 4X1, Canada", text.getText());

        //logout from driver, into rider
        solo.clickOnView(solo.getView(R.id.profile));
        solo.clickOnView(solo.getView(R.id.logout));
        solo.sleep(1000);
        solo.enterText((EditText) solo.getView(R.id.email_login), "testRider@rider.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "password");
        solo.clickOnView(solo.getView(R.id.login_button));
        solo.clickOnView(solo.getView(R.id.rider_accept_offer_button));

        //check rider recieved offer
        solo.sleep(1000);
        text = (TextView) solo.getView(R.id.rider_main_driver_name);
        assertEquals("testDriver", text.getText());
        text = (TextView) solo.getView(R.id.rider_main_driver_email);
        assertEquals("testDriver@driver.com", text.getText());

        //logout, back to driver
        solo.clickOnView(solo.getView(R.id.profile));
        solo.clickOnView(solo.getView(R.id.logout));
        solo.sleep(1000);
        solo.enterText((EditText) solo.getView(R.id.email_login), "testDriver@driver.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "password");
        solo.clickOnView(solo.getView(R.id.login_button));


        //commence ride
        solo.clickOnView(solo.getView(R.id.request_accepted_button));
        solo.sleep(100);
        solo.clickOnView(solo.getView(R.id.profile));
        solo.clickOnView(solo.getView(R.id.logout));

        //back to rider again
        solo.enterText((EditText) solo.getView(R.id.email_login), "testRider@rider.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "password");
        solo.clickOnView(solo.getView(R.id.login_button));

        //check ride is in progress
        solo.sleep(1000);
        text = (TextView) solo.getView(R.id.rider_main_driver_name);
        assertEquals("testDriver", text.getText());
        text = (TextView) solo.getView(R.id.rider_main_driver_email);
        assertEquals("testDriver@driver.com", text.getText());

        //now finish the ride
        solo.clickOnView(solo.getView(R.id.driver_complete_ride_button));
        solo.clickOnView(solo.getView(android.R.id.button1));

        //ensure that ride is finished
        text = (TextView) solo.getView(R.id.driver_status);
        assertEquals("You currently have no ride request.", text.getText());


        //close and logout
        //sleep so firebase updates
        solo.sleep(1000);
        solo.clickOnView(solo.getView(R.id.profile));
        solo.clickOnView(solo.getView(R.id.logout));

    }
}
