package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

import static android.app.Activity.RESULT_OK;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;
import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;


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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        prefsManager = SharedPrefsManager.getInstance(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpUserProfile();
        mProfileTelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.my_profile_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.profile_submit:
                verifyAndSaveProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void verifyAndSaveProfile() {
        if (verifyAllFields()) {
            saveUserProfile();
        }
    }

    public void saveUserProfile() {
        String name = mProfileName.getText().toString();
        String email = mProfileEmail.getText().toString();
        String phone = mProfileTelephone.getText().toString();

        Profile profile = new Profile(name, email, phone);

        prefsManager.saveUserDetails(profile);

        getActivity().setResult(RESULT_OK);

        Toast.makeText(getContext(), "User profile saved. " +
                "Implement sending logic.", Toast.LENGTH_SHORT).show();
    }

    public boolean verifyAllFields() {
        verifyProfileName();
        verifyProfileEmail();
        verifyProfileTelephone();

        return mProfileName.getError() == null &&
                mProfileEmail.getError() == null &&
                mProfileTelephone.getError() == null;
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

    @OnEditorAction(R.id.profile_telephone)
    public boolean onTelephoneEditorAction(int actionId, KeyEvent event) {
        if (actionId == IME_ACTION_DONE) {
            verifyAndSaveProfile();

            return true;
        }

        return false;
    }

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

    public boolean isEditedByUser() {
        if (mProfileName == null || mProfileEmail == null || mProfileTelephone == null) {
            return true;
        }

        String name = mProfileName.getText().toString();
        String email = mProfileEmail.getText().toString();
        String telephone = mProfileTelephone.getText().toString();

        if (name.length() == 0 && email.length() == 0 && telephone.length() == 0) {
            return true;
        }

        if (!prefsManager.getIsUserAnonymous()) {
            Profile oldProfile = prefsManager.getUserProfile();

            Profile newProfile = new Profile(name, email, telephone);

            return newProfile.equals(oldProfile);
        }

        return false;
    }

}
