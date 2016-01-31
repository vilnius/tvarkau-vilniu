package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.SharedPrefsManager;


/**
 * An activity representing a list of Problems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProblemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends BaseActivity {
    @Bind(R.id.home_btn_sign_in)
    TextView mLoginButton;

    SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ButterKnife.bind(this);

        prefsManager = SharedPrefsManager.getInstance(this);
        handleSignInButton();
    }

    private void handleSignInButton() {
        if (!prefsManager.getIsUserAnonymous()) {
            mLoginButton.setVisibility(View.GONE);
        }
    }


    protected void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);

        startActivity(intent);
    }

    @OnClick(R.id.home_btn_sign_in)
    protected void onSignInButtonClicked() {
        startNewActivity(SignInActivity.class);
    }

    @OnClick(R.id.home_report_problem)
    protected void onNewIssueClicked() {
        startNewActivity(NewProblemActivity.class);
    }

    @OnClick(R.id.home_list_of_problems)
    protected void onProblemsListClicked() {
        startNewActivity(ProblemsListActivity.class);
    }

    @OnClick(R.id.home_map_of_problems)
    protected void onProblemsMapClicked() {
        startNewActivity(ProblemsMapActivity.class);
    }

    @OnClick(R.id.home_my_profile)
    protected void onMyProfileClicked() {
        startNewActivity(MyProfileActivity.class);
    }
}
