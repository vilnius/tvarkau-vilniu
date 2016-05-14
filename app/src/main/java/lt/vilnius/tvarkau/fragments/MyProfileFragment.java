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
import lt.vilnius.tvarkau.utils.SharedPrefsManager;


public class MyProfileFragment extends Fragment {

    public static MyProfileFragment getInstance() {
        return new MyProfileFragment();
    }

    private SharedPrefsManager prefsManager;

    @Bind(R.id.profile_name)
    EditText mProfileName;

//    @Bind(R.id.profile_email)
//    EditText mProfileEmail;
//
//    @Bind(R.id.profile_telephone)
//    EditText mProfileTelephone;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefsManager = SharedPrefsManager.getInstance(getContext());

        setUpUserProfile();
    }

    private void setUpUserProfile() {

        if(!prefsManager.getIsUserAnonymous()) {
//            Profile profile;
//
//            profile = Profile.returnProfile(getContext());
//            mProfileName.setText(profile.getName());
//            mProfileEmail.setText(profile.getEmail());
        }
    }
}
