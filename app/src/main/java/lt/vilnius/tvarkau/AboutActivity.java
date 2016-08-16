package lt.vilnius.tvarkau;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.utils.FormatUtils;
import lt.vilnius.tvarkau.utils.GlobalConsts;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.version_code)
    TextView versionCode;

    @BindView(R.id.thanks_to_contributors)
    TextView thanksToContributors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String version = String.format(Locale.getDefault(), "%s.%d",
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        );
        versionCode.setText(version);

        Linkify.addLinks(thanksToContributors, Linkify.WEB_URLS);
        FormatUtils.removeUnderlines(thanksToContributors);
        thanksToContributors.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @OnClick(R.id.rate_app)
    protected void onRateAppClick() {
        // For testing we can use previous app's package name
        // String testPackageName = "uk.co.es4b.tvarkau_vilniu";

        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    @OnClick(R.id.contribute_with_code)
    protected void onClickContributeWithCode() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_TRELLO));
        startActivity(intent);
    }

    @OnClick(R.id.report_bug)
    protected void onReportBugClick() {
        openEmail(GlobalConsts.CODE_FOR_VILNIUS_EMAIL);
    }

    @OnClick(R.id.visit_our_website)
    protected void onVisitOurWebsiteClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_WEBSITE));
        startActivity(intent);
    }

    @OnClick(R.id.facebook_page)
    protected void onFacebookPageClick() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + GlobalConsts.CODE_FOR_VILNIUS_FACEBOOK_ID)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_FACEBOOK)));
        }
    }

    @OnClick(R.id.meetup_page)
    protected void onMeetupPageClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_MEETUP));
        startActivity(intent);
    }

    @OnClick(R.id.phone_select_view)
    protected void onPhoneViewClick() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + GlobalConsts.VILNIUS_MUNICIPALITY_PHONE));
        startActivity(intent);
    }

    @OnClick(R.id.municipality_email)
    protected void onMunicipalityEmailClick() {
        openEmail(GlobalConsts.VILNIUS_MUNICIPALITY_EMAIL);
    }

    private void openEmail(String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.contact_email_subject));
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setMessage(this.getResources().getString(R.string.send_email_to) + " " + emailAddress)
                .setPositiveButton(R.string.ok, (dialog, whichButton) ->
                    AboutActivity.super.onBackPressed())
                .create()
                .show();
        }
    }
}
