package lt.vilnius.tvarkau.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatDialogFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.vilnius.tvarkau.R;
import lt.vilnius.tvarkau.analytics.Analytics;
import lt.vilnius.tvarkau.backend.*;
import lt.vilnius.tvarkau.entity.LoginResponse;
import lt.vilnius.tvarkau.entity.Problem;
import lt.vilnius.tvarkau.events_listeners.NewProblemAddedEvent;
import lt.vilnius.tvarkau.fragments.interactors.MyReportsInteractor;
import lt.vilnius.tvarkau.fragments.interactors.SharedPreferencesMyReportsInteractor;
import lt.vilnius.tvarkau.prefs.Preferences;
import lt.vilnius.tvarkau.rx.RxBus;
import lt.vilnius.tvarkau.utils.EncryptUtils;
import lt.vilnius.tvarkau.utils.FormatUtils;
import lt.vilnius.tvarkau.utils.KeyboardUtils;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;
import org.threeten.bp.LocalDateTime;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

public class ReportImportDialogFragment extends DaggerAppCompatDialogFragment {

    @Inject
    LegacyApiService legacyApiService;
    @Inject
    Analytics analytics;
    @Inject
    @Named(Preferences.MY_PROBLEMS_PREFERENCES)
    SharedPreferences myProblemsPreferences;

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

    private Unbinder unbinder;
    private SharedPrefsManager prefsManager;
    private String password;
    private Disposable disposable;
    private MyReportsInteractor myReportsInteractor;

    public ReportImportDialogFragment() {
    }

    public static ReportImportDialogFragment newInstance() {
        return new ReportImportDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = SharedPrefsManager.getInstance(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myReportsInteractor = new SharedPreferencesMyReportsInteractor(myProblemsPreferences);
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

        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefsManager.changeUserRememberMeStatus(isChecked);
            }
        });

        vilniusAccountLoginError.setVisibility(View.GONE);

        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton(R.string.ok, null);

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (vilniusAccountEmail.hasFocus()) {
                    KeyboardUtils.closeSoftKeyboard(ReportImportDialogFragment.this.getActivity(), vilniusAccountEmail);
                }
                if (vilniusAccountPassword.hasFocus()) {
                    KeyboardUtils.closeSoftKeyboard(ReportImportDialogFragment.this.getActivity(), vilniusAccountPassword);
                }
                dialog.dismiss();
            }
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

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ReportImportDialogFragment.this.validateVilniusSignInputFields()) {

                        String email = vilniusAccountEmail.getText().toString();

                        password = vilniusAccountPassword.getText().toString();
                        String encodedPassword = EncryptUtils.encrypt(password);

                        GetVilniusSignParams params = new GetVilniusSignParams(email, encodedPassword);

                        ApiRequest<GetVilniusSignParams> request = new ApiRequest<>(ApiMethod.LOGIN, params);

                        Consumer<ApiResponse<LoginResponse>> onSuccess = new Consumer<ApiResponse<LoginResponse>>() {
                            @Override
                            public void accept(ApiResponse<LoginResponse> apiResponse) {
                                vilniusAccountLoginError.setVisibility(View.GONE);
                                if (apiResponse.getResult() != null) {
                                    analytics.trackLogIn();

                                    prefsManager.saveUserSessionId(apiResponse.getResult().getSessionId());
                                    prefsManager.saveUserEmail(apiResponse.getResult().getEmail());
                                    prefsManager.saveUserPassword(password);
                                    LocalDateTime localDateTime = LocalDateTime.now();
                                    prefsManager.saveUserLastReportImport(FormatUtils.formatLocalDateTime(localDateTime));
                                    ReportImportDialogFragment.this.loadUserReportsFromVilniusAccount(apiResponse.getResult().getEmail(), dialog);
                                } else {
                                    vilniusAccountLoginError.setVisibility(View.VISIBLE);
                                    vilniusAccountLoginError.setText(R.string.error_vilnius_account_invalid_credentials);
                                }
                            }
                        };

                        Consumer<Throwable> onError = new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Timber.e(throwable);
                                vilniusAccountLoginError.setVisibility(View.VISIBLE);
                                vilniusAccountLoginError.setText(R.string.error_on_vilnius_sign);
                            }
                        };

                        disposable = legacyApiService.loginToVilniusAccount(request)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        onSuccess,
                                        onError
                                );
                    }
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

    private void loadUserReportsFromVilniusAccount(String email, final Dialog dialog) {

        GetProblemsParams params = new GetProblemsParams.Builder()
                .setStart(0)
                .setLimit(100)
                .setReporterFilter(email)
                .create();

        ApiRequest<GetProblemsParams> request = new ApiRequest<>(ApiMethod.GET_PROBLEMS, params);

        Consumer<ApiResponse<List<Problem>>> onSuccess = new Consumer<ApiResponse<List<Problem>>>() {
            @Override
            public void accept(ApiResponse<List<Problem>> apiResponse) {
                if (apiResponse.getResult() != null) {
                    if (apiResponse.getResult().size() > 0) {
                        List<Problem> vilniusAccountReports = new ArrayList<>();
                        vilniusAccountReports.addAll(apiResponse.getResult());
                        for (Problem report : vilniusAccountReports) {
                            String reportId = report.getProblemId();

                            List<String> ids = myReportsInteractor.getReportIdsImmediate();
                            if (!ids.isEmpty()) {
                                for (String id : ids) {
                                    if (reportId != null && !reportId.equals(id)) {
                                        myReportsInteractor.saveReportId(reportId);
                                    }
                                }
                            } else {
                                if (reportId != null) {
                                    myReportsInteractor.saveReportId(reportId);
                                }
                            }
                        }

                        RxBus.INSTANCE.publish(new NewProblemAddedEvent());
                        Toast.makeText(ReportImportDialogFragment.this.getContext(), R.string.report_import_done,
                                Toast.LENGTH_SHORT).show();

                        if (vilniusAccountEmail.hasFocus()) {
                            KeyboardUtils.closeSoftKeyboard(ReportImportDialogFragment.this.getActivity(), vilniusAccountEmail);
                        }
                        if (vilniusAccountPassword.hasFocus()) {
                            KeyboardUtils.closeSoftKeyboard(ReportImportDialogFragment.this.getActivity(), vilniusAccountPassword);
                        }
                        dialog.dismiss();
                    }
                } else {
                    if (vilniusAccountEmail.hasFocus()) {
                        KeyboardUtils.closeSoftKeyboard(ReportImportDialogFragment.this.getActivity(), vilniusAccountEmail);
                    }
                    if (vilniusAccountPassword.hasFocus()) {
                        KeyboardUtils.closeSoftKeyboard(ReportImportDialogFragment.this.getActivity(), vilniusAccountPassword);
                    }
                    Toast.makeText(ReportImportDialogFragment.this.getContext(), R.string.error_no_report_on_Vilnius_account,
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        };

        Consumer<Throwable> onError = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Toast.makeText(ReportImportDialogFragment.this.getContext(), R.string.error_loading_reports_from_vilnius_account,
                        Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
                Timber.e(throwable);
            }
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
        if (disposable != null) {
            disposable.dispose();
        }
        super.onDestroyView();
        unbinder.unbind();
    }
}
