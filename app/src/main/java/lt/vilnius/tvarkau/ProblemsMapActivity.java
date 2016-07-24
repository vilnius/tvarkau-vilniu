package lt.vilnius.tvarkau;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import lt.vilnius.tvarkau.factory.MapInfoWindowShownEvent;
import lt.vilnius.tvarkau.fragments.BaseMapFragment;
import lt.vilnius.tvarkau.fragments.MultipleProblemsMapFragment;
import lt.vilnius.tvarkau.fragments.SingleProblemMapFragment;
import lt.vilnius.tvarkau.utils.GlobalConsts;

public class ProblemsMapActivity extends BaseActivity {

    private Marker infoWindowMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.problems_map_activity);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            BaseMapFragment fragment;
            String fragmentTag = data.getString(GlobalConsts.KEY_MAP_FRAGMENT);

            if (fragmentTag != null && fragmentTag.equals(GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT))
                fragment = SingleProblemMapFragment.getInstance();
            else if (fragmentTag != null && fragmentTag.equals(GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT))
                fragment = MultipleProblemsMapFragment.getInstance();
            else
                return;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.problems_map_frame, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(infoWindowMarker != null && infoWindowMarker.isInfoWindowShown()) {
            infoWindowMarker.hideInfoWindow();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onEvent(MapInfoWindowShownEvent event) {
        Log.d("Event subscribed","true");
        infoWindowMarker = event.marker;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
