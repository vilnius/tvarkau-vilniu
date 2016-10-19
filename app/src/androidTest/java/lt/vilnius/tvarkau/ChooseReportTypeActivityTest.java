package lt.vilnius.tvarkau;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

/**
 * Created by Edgaras on 8/27/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ChooseReportTypeActivityTest {
    @Rule
    public IntentsTestRule<ChooseReportTypeActivity> activityTestRule =
            new IntentsTestRule<ChooseReportTypeActivity>(ChooseReportTypeActivity.class);

    @Test
    public void checkActivityRenders() {
        onView(withId(R.id.report_types_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void checkActivityFinishesAfterItemClick() {
        onView(withId(R.id.report_types_recycler_view)).perform(click());
        assertTrue(activityTestRule.getActivity().isFinishing());
    }
}