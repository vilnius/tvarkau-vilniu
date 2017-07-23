package lt.vilnius.tvarkau.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.v7.app.AlertDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import lt.vilnius.tvarkau.R

sealed class GooglePlayServicesAvailability {
}

object GooglePlayServicesAvailable : GooglePlayServicesAvailability()
data class GooglePlayServicesUserActionRequired(val resultCode: Int) : GooglePlayServicesAvailability()
data class GooglePlayServicesMissing(val resultCode: Int) : GooglePlayServicesAvailability()

fun Context.googlePlayServicesAvailability(): GooglePlayServicesAvailability {
    val resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
    return when (resultCode) {
        ConnectionResult.SUCCESS ->
            GooglePlayServicesAvailable
        ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED,
        ConnectionResult.SERVICE_DISABLED ->
            GooglePlayServicesUserActionRequired(resultCode)
        else -> GooglePlayServicesMissing(resultCode)
    }
}

fun GooglePlayServicesAvailability.available() = this is GooglePlayServicesAvailable

fun GooglePlayServicesAvailability.resultCode() =
        when (this) {
            GooglePlayServicesAvailable -> ConnectionResult.SUCCESS
            is GooglePlayServicesUserActionRequired -> resultCode
            is GooglePlayServicesMissing -> resultCode
        }

fun GooglePlayServicesAvailability.resolutionDialog(host: Activity): Dialog? =
        when (this) {
            is GooglePlayServicesUserActionRequired ->
                GoogleApiAvailability.getInstance()
                        .getErrorDialog(host, resultCode,
                                ActivityConstants.REQUEST_PLAY_SERVICES_RESTORE)
            is GooglePlayServicesMissing ->
                AlertDialog.Builder(host)
                        .setTitle(R.string.dialog_google_play_error_title)
                        .setMessage(R.string.dialog_google_play_error_description)
                        .setPositiveButton(R.string.ok, null)
                        .create()
            GooglePlayServicesAvailable -> null
        }
