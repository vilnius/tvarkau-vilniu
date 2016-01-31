package lt.vilnius.tvarkau.views.adapters;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import lt.vilnius.tvarkau.R;

/**
 * Created by Karolis Vycius on 2016-01-30.
 */
public class MapsInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    View view;

    public MapsInfoWindowAdapter(LayoutInflater layoutInflater) {
        view = layoutInflater.inflate(R.layout.problem_list_content, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return view;
    }
}
