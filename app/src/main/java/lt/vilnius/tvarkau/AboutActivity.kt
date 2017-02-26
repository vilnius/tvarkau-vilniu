package lt.vilnius.tvarkau

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.utils.DeviceUtils
import lt.vilnius.tvarkau.utils.FormatUtils
import lt.vilnius.tvarkau.utils.GlobalConsts

class AboutActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        version_code.text = DeviceUtils.appVersion

        Linkify.addLinks(thanks_to_contributors, Linkify.WEB_URLS)
        FormatUtils.removeUnderlines(thanks_to_contributors)
        thanks_to_contributors.movementMethod = LinkMovementMethod.getInstance()

        rate_app.setOnClickListener { onRateAppClick() }
        contribute_with_code.setOnClickListener { onClickContributeWithCode() }
        report_bug.setOnClickListener { onReportBugClick() }
        facebook_page.setOnClickListener { onFacebookPageClick() }
        meetup_page.setOnClickListener { onMeetupPageClick() }
        phone_select_view.setOnClickListener { onPhoneViewClick() }
        municipality_email.setOnClickListener { onMunicipalityEmailClick() }
        privacy_policy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.PRIVACY_POLICY_PAGE)))
        }
    }

    fun onRateAppClick() {
        val uri = Uri.parse("market://details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }

    }

    fun onClickContributeWithCode() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_TRELLO))
        startActivity(intent)
    }

    fun onReportBugClick() {
        openEmail(GlobalConsts.CODE_FOR_VILNIUS_EMAIL)
    }

    fun onVisitOurWebsiteClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_WEBSITE))
        startActivity(intent)
    }

    fun onFacebookPageClick() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + GlobalConsts.CODE_FOR_VILNIUS_FACEBOOK_ID)))
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_FACEBOOK)))
        }
    }

    fun onMeetupPageClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GlobalConsts.CODE_FOR_VILNIUS_MEETUP))
        startActivity(intent)
    }

    fun onPhoneViewClick() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel: ${GlobalConsts.VILNIUS_MUNICIPALITY_PHONE}"))
        startActivity(intent)
    }

    fun onMunicipalityEmailClick() {
        openEmail(GlobalConsts.VILNIUS_MUNICIPALITY_EMAIL)
    }

    private fun openEmail(emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:" + emailAddress)
        intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.contact_email_subject) + " (" +
                DeviceUtils.deviceInfo + ", " + resources.getString(R.string.app_version) + " " +
                DeviceUtils.appVersion + ")")
        if (intent.resolveActivity(this.packageManager) != null) {
            startActivity(intent)
        } else {
            AlertDialog.Builder(this, R.style.MyDialogTheme)
                    .setMessage(resources.getString(R.string.send_email_to) + " " + emailAddress)
                    .setPositiveButton(R.string.ok) { dialog, whichButton -> super@AboutActivity.onBackPressed() }
                    .create()
                    .show()
        }
    }
}
