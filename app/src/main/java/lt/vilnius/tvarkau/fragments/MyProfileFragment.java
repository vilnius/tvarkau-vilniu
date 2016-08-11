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

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

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
    EditText mProfileName;

    @BindView(R.id.profile_email)
    EditText mProfileEmail;

    @BindView(R.id.profile_telephone)
    EditText mProfileTelephone;

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
                saveUserProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveUserProfile() {
        String name = mProfileName.getText().toString();
        String email = mProfileEmail.getText().toString();
        String phone = mProfileTelephone.getText().toString();

        Profile profile = new Profile(name, email, phone);

        prefsManager.saveUserDetails(profile);

        getActivity().setResult(RESULT_OK);

        Toast.makeText(getContext(), "Beta versijoje registracija dar neveikia", Toast.LENGTH_SHORT).show();

        getActivity().finish();

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            KeyboardUtils.closeSoftKeyboard(getActivity(), view);
        }
    }

    private void setUpUserProfile() {
        if (!prefsManager.isUserAnonymous()) {
            Profile profile = Profile.returnProfile(getContext());

            mProfileName.setText(profile.getName());
            mProfileEmail.setText(profile.getEmail());
            mProfileTelephone.setText(profile.getMobilePhone());
        }
    }

    public boolean isEditedByUser() {
        if (mProfileName == null || mProfileEmail == null || mProfileTelephone == null) {
            return true;
        }

        String name = mProfileName.getText().toString();
        String email = mProfileEmail.getText().toString();
        String telephone = mProfileTelephone.getText().toString();

        if (!prefsManager.isUserAnonymous()) {
            Profile oldProfile = prefsManager.getUserProfile();

            Profile newProfile = new Profile(name, email, telephone);

            return newProfile.equals(oldProfile);
        }

        return false;
    }

    public void fillFields(GoogleSignInAccount account) {
        Profile profile = new Profile(account);

        mProfileName.setText(profile.getName());
        mProfileEmail.setText(profile.getEmail());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unbinder.unbind();
    }
}
