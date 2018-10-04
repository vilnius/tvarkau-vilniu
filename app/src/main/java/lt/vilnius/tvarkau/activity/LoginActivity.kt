package lt.vilnius.tvarkau.activity

import android.Manifest.permission.READ_CONTACTS
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.MainActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.extensions.withViewModel
import lt.vilnius.tvarkau.viewmodel.ContactDataLiveData
import lt.vilnius.tvarkau.viewmodel.LoginViewModel

class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        populateAutoComplete()
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                viewModel.attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { viewModel.attemptLogin() }
        sign_in_skip_button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        viewModel = withViewModel(viewModelFactory) {

        }
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

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(
                email,
                R.string.permission_rationale,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(
                    android.R.string.ok
                ) {
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
    }
}
