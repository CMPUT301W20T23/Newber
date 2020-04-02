package com.cmput301w20t23.newber;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301w20t23.newber.views.LoginActivity;
import com.cmput301w20t23.newber.views.MainActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test class for LoginActivity.
 * Robotium test framework is used.
 * @author Arthur Nonay
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginActivityTest {
    private Solo solo;

    /**
     * Tells the test to start in the login activity
     */
    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the activity.
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    @Test
    /**
     * Attempt to login with invalid login credentials.
     */
    public void failedLoginTest() {
        // current activity must be LoginActivity
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);

        // enter invalid login credentials and attempt login
        solo.enterText((EditText) solo.getView(R.id.email_login), "testLogin@intent.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "incorrectPassword");
        solo.clickOnButton("Login");

        // wait up to 1 sec for toast message
        Date date = new Date();
        TextView toast;
        long elapsed;
        do {
            elapsed = new Date().getTime() - date.getTime();
            toast = (TextView) solo.getView(android.R.id.message);
        } while (elapsed <= 1000 && toast != null);

        // check if appropriate toast is displayed
        assertEquals(toast.getText().toString(), "Invalid credentials");

        // current activity must remain LoginActivity
        solo.assertCurrentActivity("Wrong activity after login attempt", LoginActivity.class);
    }

    /**
     * Attempt to login with valid login credentials and assert activity switch to MainActivity.
     */
    @Test
    public void successfulLoginTest() {
        // current activity must be LoginActivity
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);

        // enter valid login credentials and attempt login
        solo.enterText((EditText) solo.getView(R.id.email_login), "testLogin@intent.com");
        solo.enterText((EditText) solo.getView(R.id.password_login), "correctPassword");
        solo.clickOnButton("Login");

        // current activity must switch to MainActivity
        solo.assertCurrentActivity("Wrong activity after login attempt", MainActivity.class);
    }

    /**
     * Closes the activity after each test.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
