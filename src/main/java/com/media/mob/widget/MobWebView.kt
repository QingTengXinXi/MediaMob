package com.media.mob.widget

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.webkit.GeolocationPermissions
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.media.mob.helper.logger.MobLogger
import java.io.File
import java.util.Locale
import java.util.regex.Pattern

class MobWebView : WebView {

    private val classTarget = MobWebView::class.java.simpleName

    var onReceivedTitle: ((String) -> Unit)? = null

    var onProgressChanged: ((Int) -> Unit)? = null

    var onWebPageStarted: (() -> Unit)? = null
    var onWebPageFinished: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled") fun init() {
        settings.useWideViewPort = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.mediaPlaybackRequiresUserGesture = false
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.setGeolocationEnabled(true)
        settings.setSupportMultipleWindows(false)
        settings.setAppCacheEnabled(true)
        settings.setAppCachePath(File(context.cacheDir, "mob_web").path)
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        webViewClient = object : WebViewClient() {

            var loadingFinished = true

            var redirect = false

            override fun onPageFinished(
                view: WebView,
                url: String
            ) {
                MobLogger.e(classTarget, "PageFinished: $url : ${System.currentTimeMillis()}")

                if (!redirect) {
                    loadingFinished = true
                    onWebPageFinished?.invoke()
                } else {
                    redirect = false
                }
            }

            override fun onPageStarted(
                view: WebView,
                url: String,
                favicon: Bitmap?
            ) {
                loadingFinished = false

                onWebPageStarted?.invoke()

                MobLogger.e(classTarget, "PageStarted: $url : ${System.currentTimeMillis()}")
            }

            /**
             * 系统可以处理的url正则
             */
            private val ACCEPTED_URI_SCHEME = Pattern.compile(
                "(?i)"
                    +
                    '('.toString()
                    +
                    "(?:http|https|ftp|file)://" + "|(?:inline|data|about|javascript):" + "|(?:.*:.*@)"
                    + ')'.toString() + "(.*)"
            )

            /**
             * 该url是否属于浏览器能处理的内部协议
             */
            private fun isAcceptedScheme(url: String): Boolean {
                if (url.startsWith("intent://") || url.startsWith("android-app://")) {
                    return false
                }
                val lowerCaseUrl = url.toLowerCase(Locale.ROOT)
                val acceptedUrlSchemeMatcher = ACCEPTED_URI_SCHEME.matcher(lowerCaseUrl)
                return acceptedUrlSchemeMatcher.matches()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                if (!loadingFinished) {
                    redirect = true
                }
                loadingFinished = false

                if (!isAcceptedScheme(url)) {
                    MobLogger.i(classTarget, "DeepLink: $url")
                    try {
                        val intent = if (url.startsWith("android-app://")) {
                            Intent.parseUri(url, Intent.URI_ANDROID_APP_SCHEME)
                        } else {
                            Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        }
                        intent.component = null
                        intent.selector = null

                        if (context.packageManager.resolveActivity(intent, 0) != null) {
                            context.startActivity(intent)
                        } else {
                            MobLogger.w(classTarget, "intent cant open: $intent")
                        }
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }

                    return true
                }
                return false
            }

            override fun onPageCommitVisible(
                view: WebView?,
                url: String
            ) {
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                handler?.proceed()
                MobLogger.e(classTarget, error.toString())
            }
        }

        webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        callback?.invoke(origin, true, true)
                    } else {
                        callback?.invoke(origin, false, true)
                    }
                } else {
                    callback?.invoke(origin, true, true)
                }
            }

            override fun onReceivedTitle(
                view: WebView?,
                title: String?
            ) {
                onReceivedTitle?.invoke(title ?: "")
            }

            override fun onProgressChanged(
                view: WebView?,
                newProgress: Int
            ) {
                onProgressChanged?.invoke(newProgress)
            }
        }
    }
}