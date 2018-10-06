package lt.vilnius.tvarkau.activity

import android.Manifest.permission.READ_CONTACTS
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.design.widget.Snackbar.LENGTH_SHORT
import android.support.design.widget.Snackbar.make
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_login.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.component.ActivityComponent
import lt.vilnius.tvarkau.entity.User
import lt.vilnius.tvarkau.extensions.goneIf
import lt.vilnius.tvarkau.extensions.observeNonNull
import lt.vilnius.tvarkau.extensions.visibleIf
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.prefs.AppPreferences
import lt.vilnius.tvarkau.repository.Status
import lt.vilnius.tvarkau.viewmodel.ContactDataLiveData
import lt.vilnius.tvarkau.viewmodel.LoginViewModel
import javax.inject.Inject


class LoginActivity : BaseActivity() {

    @Inject
    lateinit var appPreferences: AppPreferences

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (appPreferences.apiToken.isSet()) {
            startMainActivity()
            return
        }

        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                viewModel.attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { viewModel.attemptLogin() }
        skip_sign_in_button.setOnClickListener { viewModel.signInGuestUser() }
        google_sign_in_button.setOnClickListener { signInWithGoogle() }

        viewModel = withViewModel(viewModelFactory) {
            observeNonNull(errorEvents) {
                Snackbar.make(findViewById<View>(android.R.id.content), it.message ?: "", LENGTH_LONG).show()
            }
            observeNonNull(networkState) {
                when (it.status) {
                    Status.RUNNING -> showProgress(true)
                    Status.SUCCESS, Status.FAILED -> showProgress(false)
                }
            }
            observeNonNull(loggedInUser, ::onSuccessfulSignIn)
        }
    }

    override fun onInject(component: ActivityComponent) {
        component.inject(this)
    }

    private fun onSuccessfulSignIn(user: User) {
        val greeting = if (user.email != null) {
            getString(R.string.sign_in_welcome, user.email)
        } else {
            getString(R.string.sign_in_welcome_guest)
        }

        Snackbar.make(findViewById<View>(android.R.id.content), greeting, LENGTH_SHORT).show()
        startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }

        ContactDataLiveData(this).observe(this, Observer {
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, it)
            email.setAdapter(adapter)
        })
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.google_client_id))
            .build()

        val existingAccount = GoogleSignIn.getLastSignedInAccount(this)
        if (existingAccount != null) {
            viewModel.signInWithExistingGoogleAccount(existingAccount)
        } else {
            val client = GoogleSignIn.getClient(this, gso)
            startActivityForResult(client.signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_SIGN_IN -> viewModel.signInWithGoogle(data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            make(
                email,
                R.string.permission_rationale,
                LENGTH_INDEFINITE
            )
                .setAction(android.R.string.ok) {
                    requestPermissions(
                        arrayOf(READ_CONTACTS),
                        REQUEST_READ_CONTACTS
                    )
                }
        } else {
            requestPermissions(
                arrayOf(READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
        return false
    }

    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        login_form.goneIf(show)
        login_form.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 0 else 1).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_form.goneIf(show)
                }
            })

        login_progress.visibleIf(show)
        login_progress.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    login_progress.visibleIf(show)
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    companion object {
        private const val REQUEST_READ_CONTACTS = 0

        private const val RC_SIGN_IN = 1001
    }
}
