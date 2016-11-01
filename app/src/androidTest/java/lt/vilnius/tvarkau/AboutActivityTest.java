package lt.vilnius.tvarkau;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;

/**
 * Created by Edgaras on 8/27/2016.
 */
@RunWith(AndroidJUnit4.class)
public class AboutActivityTest {
    @Rule
    public IntentsTestRule<AboutActivity> activityTestRule =
            new IntentsTestRule<AboutActivity>(AboutActivity.class);

    @Test
    public void checkActivityRenders() {
        onView(withId(R.id.about_text_below)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

    @Test
    public void checkRateButtonClickLaunchedPlayStore() {
        onView(withId(R.id.rate_app)).perform(scrollTo(), click());
        String expectedBrowserUrl = "http://play.google.com/store/apps/details?id=lt.vilnius.tvarkau";
        String expectedMarketUri = "market://details?id=lt.vilnius.tvarkau";
        intended(allOf(hasAction(Intent.ACTION_VIEW), anyOf(hasData(expectedMarketUri), hasData(expectedBrowserUrl))));
    }
}