package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;
import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;


public class MyProfileFragment extends Fragment {

    public static MyProfileFragment getInstance() {
        return new MyProfileFragment();
    }

    private SharedPrefsManager prefsManager;

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefsManager = SharedPrefsManager.getInstance(getContext());

        setUpUserProfile();
    }

    private void setUpUserProfile() {
        if (!prefsManager.getIsUserAnonymous()) {
            Profile profile;

            profile = Profile.returnProfile(getContext());
            mProfileName.setText(profile.getName());
            mProfileEmail.setText(profile.getEmail());
            mProfileTelephone.setText(profile.getMobilePhone());
        }
    }

    @OnTextChanged(value = R.id.profile_name, callback = AFTER_TEXT_CHANGED)
    public void verifyProfileName() {
        if (mProfileName.getText().length() == 0) {
            mProfileName.setError(getString(R.string.error_empty_field));
        } else {
            mProfileName.setError(null);
        }
    }

    @OnTextChanged(value = R.id.profile_email, callback = AFTER_TEXT_CHANGED)
    public void verifyProfileEmail() {
        Pattern pattern = Patterns.EMAIL_ADDRESS;

        if (mProfileEmail.getText().length() == 0) {
            mProfileEmail.setError(getString(R.string.error_empty_field));
        } else if (!pattern.matcher(mProfileEmail.getText().toString()).matches()) {
            mProfileEmail.setError(getString(R.string.error_email_incorrect));
        } else {
            mProfileEmail.setError(null);
        }
    }

    @OnTextChanged(value = R.id.profile_telephone, callback = AFTER_TEXT_CHANGED)
    public void verifyProfileTelephone() {
        Pattern pattern = Patterns.PHONE;

        if (mProfileTelephone.getText().length() == 0) {
            mProfileTelephone.setError(getString(R.string.error_empty_field));
        } else if (!pattern.matcher(mProfileTelephone.getText().toString()).matches()) {
            mProfileTelephone.setError(getString(R.string.error_phone_number_incorrect));
        } else {
            mProfileTelephone.setError(null);
        }
    }

    @OnTextChanged(value = R.id.profile_telephone, callback = BEFORE_TEXT_CHANGED)
    public void formatTelephoneNumber() {
        //noinspection deprecation
        String formattedPhone = PhoneNumberUtils.formatNumber(mProfileTelephone.getText().toString());

        if(!formattedPhone.equals(mProfileTelephone.getText().toString()))
            mProfileTelephone.setText(formattedPhone);
    }
}
