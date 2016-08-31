package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.utils.KeyboardUtils;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

import static android.app.Activity.RESULT_OK;


public class MyProfileFragment extends Fragment {

    private SharedPrefsManager prefsManager;

    @BindView(R.id.profile_name)
    EditText profileName;

    @BindView(R.id.profile_email)
    EditText profileEmail;

    @BindView(R.id.profile_telephone)
    EditText profileTelephone;

    private Unbinder unbinder;

    public MyProfileFragment() {
    }

    public static MyProfileFragment getInstance() {
        return new MyProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile, container, false);

        unbinder = ButterKnife.bind(this, view);

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
        profileTelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
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
                saveUserProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveUserProfile() {
        String name = profileName.getText().toString();
        String email = profileEmail.getText().toString();
        String phone = profileTelephone.getText().toString();

        Profile profile = new Profile(name, email, phone);

        prefsManager.saveUserDetails(profile);

        getActivity().setResult(RESULT_OK);

        Toast.makeText(getContext(), R.string.error_registration_not_working, Toast.LENGTH_LONG).show();

        getActivity().finish();

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            KeyboardUtils.closeSoftKeyboard(getActivity(), view);
        }
    }

    private void setUpUserProfile() {
        if (!prefsManager.isUserAnonymous()) {
            Profile profile = Profile.returnProfile(getContext());

            profileName.setText(profile.getName());
            profileEmail.setText(profile.getEmail());
            profileTelephone.setText(profile.getMobilePhone());
        }
    }

    public boolean isEditedByUser() {
        if (profileName == null || profileEmail == null || profileTelephone == null) {
            return true;
        }

        String name = profileName.getText().toString();
        String email = profileEmail.getText().toString();
        String telephone = profileTelephone.getText().toString();

        if (!prefsManager.isUserAnonymous()) {
            Profile oldProfile = prefsManager.getUserProfile();

            Profile newProfile = new Profile(name, email, telephone);

            return newProfile.equals(oldProfile);
        }

        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}
