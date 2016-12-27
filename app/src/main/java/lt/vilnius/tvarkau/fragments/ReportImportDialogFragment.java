package lt.vilnius.tvarkau.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.TvarkauApplication;
import lt.vilnius.tvarkau.backend.ApiMethod;
import lt.vilnius.tvarkau.backend.ApiRequest;
import lt.vilnius.tvarkau.backend.ApiResponse;
import lt.vilnius.tvarkau.backend.GetProblemsParams;
import lt.vilnius.tvarkau.backend.GetVilniusSignParams;
import lt.vilnius.tvarkau.backend.LegacyApiService;
import lt.vilnius.tvarkau.entity.LoginResponse;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent;
import lt.vilnius.tvarkau.utils.EncryptUtils;
import lt.vilnius.tvarkau.utils.FormatUtils;
import lt.vilnius.tvarkau.utils.KeyboardUtils;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ReportImportDialogFragment extends DialogFragment {

    @Inject LegacyApiService legacyApiService;
    @Inject SharedPreferences myProblemsPreferences;

    @BindView(R.id.vilnius_account_email)
    EditText vilniusAccountEmail;

    @BindView(R.id.vilnius_account_email_wrapper)
    TextInputLayout vilniusAccountEmailWrapper;

    @BindView(R.id.vilnius_account_password)
    EditText vilniusAccountPassword;

    @BindView(R.id.vilnius_account_password_wrapper)
    TextInputLayout vilniusAccountPasswordWrapper;

    @BindView(R.id.vilnius_account_remember_me)
    CheckBox rememberMe;

    @BindView(R.id.vilnius_account_login_error)
    TextView vilniusAccountLoginError;

    private static final String IS_SETTINGS_ACTIVITY = "is_settings_activity";
    private Unbinder unbinder;
    private SharedPrefsManager prefsManager;
    private String password;
    private boolean isSettingsActivity;
    private Subscription subscription;

    public ReportImportDialogFragment() {}

    public static ReportImportDialogFragment newInstance(boolean settingsActivity) {
        ReportImportDialogFragment importDialogFragment = new ReportImportDialogFragment();

        Bundle arguments = new Bundle();
        arguments.putBoolean(IS_SETTINGS_ACTIVITY, settingsActivity);
        importDialogFragment.setArguments(arguments);

        return importDialogFragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = SharedPrefsManager.getInstance(getActivity());

        isSettingsActivity = getArguments().getBoolean(IS_SETTINGS_ACTIVITY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TvarkauApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    public interface SettingsVilniusSignInListener {
        void onVilniusSignIn();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);

        alertDialogBuilder.setTitle(getString(R.string.sign_in_to_vilnius_account));

        View view = LayoutInflater.from(getContext()).inflate(R.layout.report_import_dialog, null);
        unbinder = ButterKnife.bind(this, view);

        vilniusAccountEmail.requestFocus();

        if (prefsManager.getUserRememberMeStatus()) {
            rememberMe.setChecked(true);
            vilniusAccountEmail.setText(prefsManager.getUserEmail());
            vilniusAccountPassword.setText(prefsManager.getUserPassword());
        } else {
            rememberMe.setChecked(false);
        }

        rememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> prefsManager.changeUserRememberMeStatus(isChecked));

        vilniusAccountLoginError.setVisibility(View.GONE);

        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton(R.string.ok, null);

        alertDialogBuilder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            if (vilniusAccountEmail.hasFocus()) {
                KeyboardUtils.closeSoftKeyboard(getActivity(), vilniusAccountEmail);
            }
            if (vilniusAccountPassword.hasFocus()) {
                KeyboardUtils.closeSoftKeyboard(getActivity(), vilniusAccountPassword);
            }
            dialog.dismiss();
        });

        Dialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {

            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);

            positiveButton.setOnClickListener(view -> {

                if (validateVilniusSignInputFields()) {

                    String email = vilniusAccountEmail.getText().toString();

                    password = vilniusAccountPassword.getText().toString();
                    String encodedPassword = EncryptUtils.encrypt(password);

                    GetVilniusSignParams params = new GetVilniusSignParams(email, encodedPassword);

                    ApiRequest<GetVilniusSignParams> request = new ApiRequest<>(ApiMethod.LOGIN, params);

                    Action1<ApiResponse<LoginResponse>> onSuccess = apiResponse -> {
                        vilniusAccountLoginError.setVisibility(View.GONE);
                        if (apiResponse.getResult() != null) {
                            prefsManager.saveUserSessionId(apiResponse.getResult().getSessionId());
                            prefsManager.saveUserEmail(apiResponse.getResult().getEmail());
                            prefsManager.saveUserPassword(password);
                            LocalDateTime localDateTime = LocalDateTime.now();
                            prefsManager.saveUserLastReportImport(FormatUtils.formatLocalDateTime(localDateTime));
                            loadUserReportsFromVilniusAccount(apiResponse.getResult().getEmail(), dialog);
                        } else {
                            vilniusAccountLoginError.setVisibility(View.VISIBLE);
                            vilniusAccountLoginError.setText(R.string.error_vilnius_account_invalid_credentials);
                        }
                    };

                    Action1<Throwable> onError = throwable -> {
                        Timber.e(throwable);
                        vilniusAccountLoginError.setVisibility(View.VISIBLE);
                        vilniusAccountLoginError.setText(R.string.error_on_vilnius_sign);
                    };

                    subscription = legacyApiService.loginToVilniusAccount(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            onSuccess,
                            onError
                        );
                }
            });
        }
    }

    private boolean validateVilniusSignInputFields() {
        boolean emailIsValid = false;
        boolean passwordIsValid = false;

        if (vilniusAccountEmail.getText().toString().isEmpty()) {
            vilniusAccountEmailWrapper.setError(getText(R.string.error_fill_vilnius_account_email));
        } else if (Patterns.EMAIL_ADDRESS.matcher(vilniusAccountEmail.getText()).matches()) {
            vilniusAccountEmailWrapper.setError(null);
            emailIsValid = true;
        } else {
            vilniusAccountEmailWrapper.setError(getText(R.string.error_vilnius_account_email_invalid));
        }

        if (vilniusAccountPassword.getText().toString().isEmpty()) {
            vilniusAccountPasswordWrapper.setError(getText(R.string.error_fill_vilnius_account_password));
        } else {
            vilniusAccountPasswordWrapper.setError(null);
            passwordIsValid = true;
        }

        return emailIsValid && passwordIsValid;
    }

    private void loadUserReportsFromVilniusAccount(String email, Dialog dialog) {

        GetProblemsParams params = new GetProblemsParams.Builder()
            .setStart(0)
            .setLimit(100)
            .setReporterFilter(email)
            .create();

        ApiRequest<GetProblemsParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEMS, params);

        Action1<ApiResponse<List<Problem>>> onSuccess = apiResponse -> {
            if (apiResponse.getResult() != null) {
                if (apiResponse.getResult().size() > 0) {
                    List<Problem> vilniusAccountReports = new ArrayList<>();
                    vilniusAccountReports.addAll(apiResponse.getResult());
                    for (Problem report : vilniusAccountReports) {
                        String reportId = report.getProblemId();
                        if (!myProblemsPreferences.getAll().isEmpty()) {
                            for (String key : myProblemsPreferences.getAll().keySet()) {
                                if (!reportId.equals(myProblemsPreferences.getString(key, ""))) {
                                    myProblemsPreferences
                                        .edit()
                                        .putString(NewReportFragment.PROBLEM_PREFERENCE_KEY + reportId, reportId)
                                        .apply();
                                }
                            }
                        } else {
                            myProblemsPreferences
                                .edit()
                                    .putString(NewReportFragment.PROBLEM_PREFERENCE_KEY + reportId, reportId)
                                .apply();
                        }
                    }

                    if (isSettingsActivity) {
                        ((SettingsVilniusSignInListener) getActivity()).onVilniusSignIn();
                    } else {
                        EventBus.getDefault().post(new NewProblemAddedEvent());
                        Toast.makeText(getContext(), R.string.report_import_done,
                            Toast.LENGTH_SHORT).show();
                    }

                    if (vilniusAccountEmail.hasFocus()) {
                        KeyboardUtils.closeSoftKeyboard(getActivity(), vilniusAccountEmail);
                    }
                    if (vilniusAccountPassword.hasFocus()) {
                        KeyboardUtils.closeSoftKeyboard(getActivity(), vilniusAccountPassword);
                    }
                    dialog.dismiss();
                }
            } else {
                if (vilniusAccountEmail.hasFocus()) {
                    KeyboardUtils.closeSoftKeyboard(getActivity(), vilniusAccountEmail);
                }
                if (vilniusAccountPassword.hasFocus()) {
                    KeyboardUtils.closeSoftKeyboard(getActivity(), vilniusAccountPassword);
                }
                Toast.makeText(getContext(), R.string.error_no_report_on_Vilnius_account,
                    Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        };

        Action1<Throwable> onError = throwable -> {
            Toast.makeText(getContext(), R.string.error_loading_reports_from_vilnius_account,
                Toast.LENGTH_SHORT).show();
            throwable.printStackTrace();
            Timber.e(throwable);
        };

        legacyApiService.getProblems(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                onSuccess,
                onError
            );
    }

    @Override
    public void onDestroyView() {
        if(subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroyView();
        unbinder.unbind();
    }
}
