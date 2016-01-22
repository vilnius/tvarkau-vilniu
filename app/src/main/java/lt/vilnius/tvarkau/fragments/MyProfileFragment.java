package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.vilnius.tvarkau.R;

/**
 * Created by Karolis Vycius on 2016-01-21.
 */
public class MyProfileFragment extends Fragment {

    public static MyProfileFragment getInstance() {
        return new MyProfileFragment();
    }

    @Bind(R.id.profile_name)
    EditText mProfileName;

    @Bind(R.id.profile_email)
    EditText mProfileEmail;

    @Bind(R.id.profile_telephone)
    EditText mProfileTelephone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile, container, false);

        ButterKnife.bind(this, view);
        return view;
    }
}
