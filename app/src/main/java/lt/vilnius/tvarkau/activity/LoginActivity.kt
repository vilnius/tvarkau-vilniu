package lt.vilnius.tvarkau.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_login.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.entity.User
import lt.vilnius.tvarkau.extensions.goneIf
import lt.vilnius.tvarkau.extensions.observeNonNull
import lt.vilnius.tvarkau.extensions.visibleIf
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.repository.Status
import lt.vilnius.tvarkau.viewmodel.LoginViewModel

class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

    private fun onSuccessfulSignIn(user: User) {
        startMainActivity()
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
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

    companion object {
        private const val RC_SIGN_IN = 1001
    }
}
