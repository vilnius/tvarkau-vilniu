package lt.vilnius.tvarkau.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_viisp_login.*
import kotlinx.android.synthetic.main.app_bar.*
import lt.vilnius.tvarkau.BaseActivity
import lt.vilnius.tvarkau.R
import lt.vilnius.tvarkau.dagger.module.RestAdapterModule
import lt.vilnius.tvarkau.extensions.gone
import okhttp3.HttpUrl
import javax.inject.Inject
import javax.inject.Named

class ViispLoginActivity : BaseActivity() {

    @field:[Inject Named(RestAdapterModule.VIISP_AUTH_URI)]
    lateinit var viispAuthUri: String

    @SuppressLint("SetJavaScriptEnabled")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viisp_login)

        setSupportActionBar(toolbar)
        setTitle(R.string.title_sign_in_with_viisp)

        with(web_view.settings) {
            useWideViewPort = true
            javaScriptEnabled = true
            loadWithOverviewMode = true

            setAppCacheEnabled(true)
            databaseEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            }
        }

        web_view.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)

                if (title.isNullOrEmpty()) return
                if (title!!.startsWith(viispAuthUri)) return
                this@ViispLoginActivity.title = title
            }
        }

        web_view.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (url.startsWith(CALLBACK_URI)) {
                    val parsed = Uri.parse(url)!!
                    val ticket = parsed.getQueryParameter("ticket")
                    val data = Intent().apply {
                        putExtra(LoginActivity.RESULT_TICKET, ticket)
                    }

                    setResult(Activity.RESULT_OK, data)
                    finish()
                    true
                } else {
                    false
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                if (url != null && !url.startsWith(viispAuthUri)) {
                    progress_bar.gone()
                }
                super.onPageFinished(view, url)
            }
        }

        web_view.post {
            val parsed = HttpUrl.parse(viispAuthUri)!!
                .newBuilder()
                .addQueryParameter("redirect_uri", CALLBACK_URI)
                .build()

            web_view.loadUrl(parsed.toString())
        }
    }

    override fun onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val CALLBACK_URI = "lt.tvarkauvilniu://viisp/callback"
    }
}
