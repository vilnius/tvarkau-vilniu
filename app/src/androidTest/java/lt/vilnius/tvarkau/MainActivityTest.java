package lt.vilnius.tvarkau;

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by Edgaras on 8/27/2016.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> activityTestRule =
            new IntentsTestRule<MainActivity>(MainActivity.class);

    @Test
    public void checkActivityRenders() {
        onView(withId(R.id.home_about)).check(matches(isDisplayed()));
    }

    @Test
    public void checkIfReportProblemButtonStartsActivity() {
        onView(withId(R.id.home_report_problem)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), NewProblemActivity.class)));
    }

    @Test
    public void checkIfMyProblemsButtonStartsActivity() {
        onView(withId(R.id.home_my_problems)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), ProblemsListActivity.class)));
    }

    @Test
    public void checkIfAllProblemsButtonStartsActivity() {
        onView(withId(R.id.home_list_of_problems)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), ProblemsListActivity.class)));
    }

    @Test
    public void checkIfMapButtonStartsActivity() {
        onView(withId(R.id.home_map_of_problems)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), ProblemsMapActivity.class)));
    }

    @Test
    public void checkIfMyProfileButtonStartsActivity() {
        onView(withId(R.id.home_my_profile)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), MyProfileActivity.class)));
    }

    @Test
    public void checkIfAboutButtonStartsActivity() {
        onView(withId(R.id.home_about)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), AboutActivity.class)));
    }
}