package com.uablis.easyfitness;

import static com.google.common.base.Verify.verify;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testCreateAccount() {
        CreateAccountActivity activity = new CreateAccountActivity();

        activity.testCreateAccount("", "password", "confirmPassword");

        // Verify showAlert is called with the expected message for empty email
        verify(activity, times(1)).showAlert(eq("Error"), eq("All fields are required."));

        // Repeat for empty password and confirm password
    }
}