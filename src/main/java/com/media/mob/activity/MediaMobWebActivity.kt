package com.media.mob.activity

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import com.media.mob.R
import com.media.mob.widget.MediaMobWebView

class MediaMobWebActivity : AppCompatActivity() {

    companion object {
        const val WEB_URL = "mob_web_url"
    }

    private var headerView: RelativeLayout? = null

    private var titleView: TextView? = null
    private var backView: ImageView? = null

    private var webViewMedia: MediaMobWebView? = null

    private var loadingView: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mob_activity_web)

        headerView = findViewById(R.id.rl_web_header)

        titleView = findViewById(R.id.tv_web_header_title)
        backView = findViewById(R.id.iv_web_header_back)

        webViewMedia = findViewById(R.id.web_container)
        loadingView = findViewById(R.id.rl_web_loading)

        WindowCompat.setDecorFitsSystemWindows(window, true)


        backView?.setOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.mob_icon_arrow_back, null)

            backArrow?.colorFilter = PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)

            supportActionBar?.setHomeAsUpIndicator(backArrow)

        } catch (exception: Exception) {
            exception.printStackTrace()
        }

        webViewMedia?.onReceivedTitle = {
            title = it
        }

        webViewMedia?.onProgressChanged = {

        }

        webViewMedia?.onWebPageStarted = {
            if (loadingView?.visibility != View.VISIBLE) {
                loadingView?.visibility = View.VISIBLE
            }
        }

        webViewMedia?.onWebPageFinished = {
            loadingView?.visibility = View.INVISIBLE
        }

        val url = intent.getStringExtra(WEB_URL)

        if (url == null) {
            finish()
        } else {
            webViewMedia?.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        webViewMedia?.onResume()
    }

    override fun onBackPressed() {
        if (webViewMedia?.canGoBack() == true) {
            webViewMedia?.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        webViewMedia?.onPause()
    }

    override fun onDestroy() {
        webViewMedia?.destroy()
        super.onDestroy()
    }
}