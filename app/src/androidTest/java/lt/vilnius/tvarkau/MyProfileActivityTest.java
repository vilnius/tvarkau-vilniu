package lt.vilnius.tvarkau;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * Created by Edgaras on 8/27/2016.
 */
@RunWith(AndroidJUnit4.class)
public class MyProfileActivityTest {
    @Rule
    public IntentsTestRule<MyProfileActivity> activityTestRule =
            new IntentsTestRule<MyProfileActivity>(MyProfileActivity.class);

    @Test
    public void checkActivityRenders() {
        onView(withId(R.id.profile_submit)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfUserUpdatesAfterFillingInfoAndSubmitting() {
        SharedPrefsManager prefsManager = SharedPrefsManager.getInstance(activityTestRule.getActivity());
        Profile originalProfile = new Profile("test", "test@test.test", "12345678");
        onView(withId(R.id.profile_email)).perform(clearText(), typeText(originalProfile.getEmail()));
        onView(withId(R.id.profile_name)).perform(clearText(), typeText(originalProfile.getName()));
        onView(withId(R.id.profile_telephone)).perform(clearText(), typeText(originalProfile.getMobilePhone()));
        onView(withId(R.id.profile_submit)).perform(click());
        Profile savedProfile = prefsManager.getUserProfile();
        assertTrue(originalProfile.getName().equals(savedProfile.getName()));
        assertTrue(originalProfile.getEmail().equals(savedProfile.getEmail()));
    }

    //TODO Figure out how to test google sign in
}