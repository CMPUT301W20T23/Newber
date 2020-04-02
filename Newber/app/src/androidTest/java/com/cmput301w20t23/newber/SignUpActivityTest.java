package com.cmput301w20t23.newber;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.cmput301w20t23.newber.views.SignUpActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Test class for SignUpActivity.
 * Robotium test framework is used.
 * @author Arthur Nonay
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class SignUpActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, true);

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

    /**
     * Attempt to sign up without filling all fields.
     */
    @Test
    public void failedSignUpTest1() {
        // current activity must be SignUpActivity
        solo.assertCurrentActivity("Wrong activity", SignUpActivity.class);

        // enter values in some, but not all fields
        solo.enterText((EditText) solo.getView(R.id.username_sign_up), "SignUpFail");
        solo.enterText((EditText) solo.getView(R.id.phone_sign_up), "1234567890");
        solo.enterText((EditText) solo.getView(R.id.email_sign_up), "testSignUpFail@test.com");
        solo.enterText((EditText) solo.getView(R.id.password_sign_up), "testPassword");
        solo.enterText((EditText) solo.getView(R.id.confirm_password_sign_up), "testPassword");
        solo.clickOnButton("Sign Up");

        // wait up to 1 sec for toast message
        Date date = new Date();
        TextView toast;
        long elapsed;
        do {
            elapsed = new Date().getTime() - date.getTime();
            toast = (TextView) solo.getView(android.R.id.message);
        } while (elapsed <= 1000 && toast != null);

        // check if appropriate toast is displayed
        assertEquals(toast.getText().toString(), "Please enter all fields");

        // current activity must remain SignUpActivity
        solo.assertCurrentActivity("Wrong activity after sign up attempt", SignUpActivity.class);
    }

    /**
     * Attempt to sign up without selecting a role.
     */
    @Test
    public void failedSignUpTest2() {
        // current activity must be SignUpActivity
        solo.assertCurrentActivity("Wrong activity", SignUpActivity.class);

        // enter valid entries on sign up form with no role selected
        solo.enterText((EditText) solo.getView(R.id.user_first_name_sign_up), "Test");
        solo.enterText((EditText) solo.getView(R.id.user_last_name_sign_up), "User");
        solo.enterText((EditText) solo.getView(R.id.username_sign_up), "SignUpFail");
        solo.enterText((EditText) solo.getView(R.id.phone_sign_up), "1234567890");
        solo.enterText((EditText) solo.getView(R.id.email_sign_up), "testSignUpFail@test.com");
        solo.enterText((EditText) solo.getView(R.id.password_sign_up), "testPassword");
        solo.enterText((EditText) solo.getView(R.id.confirm_password_sign_up), "testPassword");
        solo.clickOnButton("Sign Up");

        // wait up to 1 sec for toast message
        Date date = new Date();
        TextView toast;
        long elapsed;
        do {
            elapsed = new Date().getTime() - date.getTime();
            toast = (TextView) solo.getView(android.R.id.message);
        } while (elapsed <= 1000 && toast != null);

        // check if appropriate toast is displayed
        assertEquals(toast.getText().toString(), "Please select an account type");

        // current activity must remain SignUpActivity
        solo.assertCurrentActivity("Wrong activity after sign up attempt", SignUpActivity.class);
    }

    /**
     * Attempt to sign up with differing entries in password and confirm password fields.
     */
    @Test
    public void failedSignUpTest3() {
        // current activity must be SignUpActivity
        solo.assertCurrentActivity("Wrong activity", SignUpActivity.class);

        // enter valid entries on sign up form with differing passwords
        solo.clickOnView(solo.getView(R.id.radio_rider));
        solo.enterText((EditText) solo.getView(R.id.user_first_name_sign_up), "Test");
        solo.enterText((EditText) solo.getView(R.id.user_last_name_sign_up), "User");
        solo.enterText((EditText) solo.getView(R.id.username_sign_up), "SignUpFail");
        solo.enterText((EditText) solo.getView(R.id.phone_sign_up), "1234567890");
        solo.enterText((EditText) solo.getView(R.id.email_sign_up), "testSignUpFail@test.com");
        solo.enterText((EditText) solo.getView(R.id.password_sign_up), "testPassword1");
        solo.enterText((EditText) solo.getView(R.id.confirm_password_sign_up), "testPassword2");
        solo.clickOnButton("Sign Up");

        // wait up to 1 sec for toast message
        Date date = new Date();
        TextView toast;
        long elapsed;
        do {
            elapsed = new Date().getTime() - date.getTime();
            toast = (TextView) solo.getView(android.R.id.message);
        } while (elapsed <= 1000 && toast != null);

        // check if appropriate toast is displayed
        assertEquals(toast.getText().toString(), "Passwords do not match");

        // current activity must remain SignUpActivity
        solo.assertCurrentActivity("Wrong activity after sign up attempt", SignUpActivity.class);
    }

    /**
     * Attempt to sign up with a taken username.
     */
    @Test
    public void failedSignUpTest4() {
        // current activity must be SignUpActivity
        solo.assertCurrentActivity("Wrong activity", SignUpActivity.class);

        solo.clickOnView(solo.getView(R.id.radio_rider));
        solo.enterText((EditText) solo.getView(R.id.user_first_name_sign_up), "Test");
        solo.enterText((EditText) solo.getView(R.id.user_last_name_sign_up), "User");
        // username taken by successfulSignUpTest()
        solo.enterText((EditText) solo.getView(R.id.username_sign_up), "testIntent");
        solo.enterText((EditText) solo.getView(R.id.phone_sign_up), "1234567890");
        solo.enterText((EditText) solo.getView(R.id.email_sign_up), "testSignUpFail@test.com");
        solo.enterText((EditText) solo.getView(R.id.password_sign_up), "testPassword");
        solo.enterText((EditText) solo.getView(R.id.confirm_password_sign_up), "testPassword");
        solo.clickOnButton("Sign Up");

        // wait up to 1 sec for toast message
        Date date = new Date();
        TextView toast;
        long elapsed;
        do {
            elapsed = new Date().getTime() - date.getTime();
            toast = (TextView) solo.getView(android.R.id.message);
        } while (elapsed <= 1000 && toast != null);

        // check if appropriate toast is displayed
        assertEquals(toast.getText().toString(), "Username has already been taken");

        // current activity must remain SignUpActivity
        solo.assertCurrentActivity("Wrong activity after sign up attempt", SignUpActivity.class);
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
