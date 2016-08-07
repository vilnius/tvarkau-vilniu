package lt.vilnius.tvarkau;

import android.os.Bundle;

import com.google.android.gms.maps.model.Marker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.events_listeners.MapInfoWindowShownEvent;
import lt.vilnius.tvarkau.fragments.BaseMapFragment;
import lt.vilnius.tvarkau.fragments.MultipleProblemsMapFragment;
import lt.vilnius.tvarkau.fragments.ProblemDetailFragment;
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

            if (fragmentTag != null && fragmentTag.equals(GlobalConsts.TAG_SINGLE_PROBLEM_MAP_FRAGMENT)) {
                Problem problem = Parcels.unwrap(data.getParcelable(ProblemDetailFragment.KEY_PROBLEM));
                fragment = SingleProblemMapFragment.getInstance(problem);
            } else if (fragmentTag != null && fragmentTag.equals(GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT))
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
        if (infoWindowMarker != null && infoWindowMarker.isInfoWindowShown()) {
            infoWindowMarker.hideInfoWindow();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void onEvent(MapInfoWindowShownEvent event) {
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
