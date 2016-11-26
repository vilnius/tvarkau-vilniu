package lt.vilnius.tvarkau;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import icepick.Icepick;
import lt.vilnius.tvarkau.backend.LegacyApiService;
import lt.vilnius.tvarkau.dagger.component.ApplicationComponent;

public abstract class BaseActivity extends AppCompatActivity {

    @Inject
    LegacyApiService legacyApiService;
    @Inject
    SharedPreferences myProblemsPreferences;

    private ApplicationComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        component = buildComponent((TvarkauApplication) getApplication());
        onInject(component);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected void onInject(ApplicationComponent component) {
        component.inject(this);
    }

    private ApplicationComponent buildComponent(TvarkauApplication application) {
        return application.getComponent();
    }
}
