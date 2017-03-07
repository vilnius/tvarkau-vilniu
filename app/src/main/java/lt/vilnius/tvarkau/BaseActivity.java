package lt.vilnius.tvarkau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import javax.inject.Inject;
import javax.inject.Named;

import lt.vilnius.tvarkau.analytics.Analytics;
import lt.vilnius.tvarkau.backend.LegacyApiService;
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent;

import static lt.vilnius.tvarkau.prefs.Preferences.MY_PROBLEMS_PREFERENCES;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    LegacyApiService legacyApiService;
    @Inject
    @Named(MY_PROBLEMS_PREFERENCES)
    SharedPreferences myProblemsPreferences;
    @Inject
    Analytics analytics;
    @Inject
    MixpanelAPI mixpanelAPI;

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    protected ApplicationComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component = buildComponent((TvarkauApplication) getApplication());
        onInject(component);
    }

    @Override
    protected void onDestroy() {
        mixpanelAPI.flush();
        super.onDestroy();
    }

    protected void onInject(ApplicationComponent component) {
        component.inject(this);
    }

    protected ApplicationComponent buildComponent(TvarkauApplication application) {
        return application.getComponent();
    }
}
