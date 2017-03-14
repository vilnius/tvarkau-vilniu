package lt.vilnius.tvarkau.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.entity.Profile;
import lt.vilnius.tvarkau.utils.KeyboardUtils;
import lt.vilnius.tvarkau.utils.PersonalCodeValidator;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;

import static android.app.Activity.RESULT_OK;


public class MyProfileFragment extends BaseFragment {

    private SharedPrefsManager prefsManager;

    @BindView(R.id.profile_name)
    EditText profileName;

    @BindView(R.id.profile_personal_code)
    EditText profilePersonalCode;

    @BindView(R.id.profile_email)
    EditText profileEmail;

    @BindView(R.id.profile_telephone)
    EditText profileTelephone;

    @BindView(R.id.profile_name_wrapper)
    TextInputLayout profileNameWrapper;

    @BindView(R.id.profile_personal_code_wrapper)
    TextInputLayout profilePersonalCodeWrapper;

    @BindView(R.id.profile_email_wrapper)
    TextInputLayout profileEmailWrapper;

    @BindView(R.id.profile_telephone_wrapper)
    TextInputLayout profileTelephoneWrapper;

    private Unbinder unbinder;
    private boolean inputsEdited;
    private String email;
    private String phone;
    private String name;
    private String personalCode;

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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputsEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        profileName.addTextChangedListener(textWatcher);
        profilePersonalCode.addTextChangedListener(textWatcher);
        profileEmail.addTextChangedListener(textWatcher);
        profileTelephone.addTextChangedListener(textWatcher);

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

    private void saveUserProfile() {

        name = profileName.getText().toString();
        email = profileEmail.getText().toString();
        phone = profileTelephone.getText().toString();
        personalCode = profilePersonalCode.getText().toString();

        if (validateProfileInputs()) {
            Profile profile = new Profile(name, email, phone, personalCode);

            prefsManager.saveUserDetails(profile);

            getActivity().setResult(RESULT_OK);

            getActivity().finish();

            prefsManager.changeUserAnonymityStatus(false);

            analytics.identifyUser(name, email, phone);
            analytics.trackPersonalDataSharingEnabled(true);

            View view = getActivity().getCurrentFocus();
            if (view != null) {
                KeyboardUtils.closeSoftKeyboard(getActivity(), view);
            }
        }
    }

    private boolean validateProfileInputs() {
        boolean nameIsValid = false;
        boolean personalCodeIsValid = false;
        boolean emailIsValid = false;
        boolean phoneIsValid = false;

        if (name != null && !name.isEmpty()) {
            if (name.trim().split("\\s+").length > 0) {
                nameIsValid = true;
                profileNameWrapper.setError(null);
            } else {
                profileNameWrapper.setError(getText(R.string.error_profile_name_invalid));
            }
        } else {
            profileNameWrapper.setError(getText(R.string.error_profile_fill_name));
        }

        if (personalCode == null) {
            profilePersonalCodeWrapper.setError(getText(R.string.error_profile_fill_personal_code));
        } else if (!PersonalCodeValidator.INSTANCE.validate(personalCode)) {
            profilePersonalCodeWrapper.setError(getText(R.string.error_new_report_invalid_personal_code));
        } else {
            personalCodeIsValid = true;
            profilePersonalCodeWrapper.setError(null);
        }

        if (email != null && !email.isEmpty()) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailIsValid = true;
                profileEmailWrapper.setError(null);
            } else {
                profileEmailWrapper.setError(getText(R.string.error_profile_email_invalid));
            }
        } else {
            profileEmailWrapper.setError(getText(R.string.error_profile_fill_email));
        }

        if (phone != null && !phone.isEmpty()) {
            phone = phone.replaceAll("[^\\d+]", "");
            String regexStr = "^[+]?[0-9]{8,12}$";
            if (!phone.matches(regexStr)) {
                profileTelephoneWrapper.setError(getText(R.string.error_profile_phone_invalid));
            } else {
                phoneIsValid = true;
                profileTelephoneWrapper.setError(null);
            }
        } else {
            profileTelephoneWrapper.setError(getText(R.string.error_profile_fill_telephone));
        }

        return nameIsValid && personalCodeIsValid && emailIsValid && phoneIsValid;
    }

    private void setUpUserProfile() {
        if (prefsManager.isUserDetailsSaved()) {
            Profile profile = prefsManager.getUserProfile();
            profileName.setText(profile.getName());
            profilePersonalCode.setText(profile.getPersonalCode());
            profileEmail.setText(profile.getEmail());
            profileTelephone.setText(profile.getMobilePhone());
        }
    }

    public boolean isEditedByUser() {

        if (!prefsManager.isUserDetailsSaved()) {
            return inputsEdited;
        } else {
            String name = null;
            String email = null;
            String telephone = null;
            String personalCode = null;

            if (profileName != null) {
                name = profileName.getText().toString();
            }
            if (profileEmail != null) {
                email = profileEmail.getText().toString();
            }
            if (profileTelephone != null) {
                telephone = profileTelephone.getText().toString();
            }
            if (profilePersonalCode != null) {
                personalCode = profilePersonalCode.getText().toString();
            }

            Profile oldProfile = prefsManager.getUserProfile();
            Profile newProfile = new Profile(name, email, telephone, personalCode);
            return !newProfile.equals(oldProfile);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
