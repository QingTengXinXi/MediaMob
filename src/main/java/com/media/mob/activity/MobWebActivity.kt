package com.media.mob.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import com.media.mob.R
import com.media.mob.widget.MobWebView

class MobWebActivity : AppCompatActivity() {

    companion object {
        const val WEB_URL = "mob_web_url"
    }

    private var toolbar: Toolbar? = null
    private var webView: MobWebView? = null

    private var loadingView: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mob_activity_web)

        toolbar = findViewById(R.id.web_header)
        webView = findViewById(R.id.web_container)
        loadingView = findViewById(R.id.rl_web_loading)

        window.statusBarColor = Color.WHITE

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setSupportActionBar(toolbar)

        toolbar?.setNavigationOnClickListener {
            finish()
        }

        toolbar?.setBackgroundColor(Color.WHITE)
        toolbar?.setTitleTextColor(Color.BLACK)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.mob_icon_arrow_back, null)

            backArrow?.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)

            supportActionBar?.setHomeAsUpIndicator(backArrow)

        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        webView?.onReceivedTitle = {
            title = it
        }

        webView?.onProgressChanged = {

        }

        webView?.onWebPageStarted = {
            if (loadingView?.visibility != View.VISIBLE) {
                loadingView?.visibility = View.VISIBLE
            }
        }

        webView?.onWebPageFinished = {
            loadingView?.visibility = View.INVISIBLE
        }

        val url = intent.getStringExtra(WEB_URL)

        if (url == null) {
            finish()
        } else {
            webView?.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }

    override fun onBackPressed() {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onDestroy() {
        webView?.destroy()
        super.onDestroy()
    }
}