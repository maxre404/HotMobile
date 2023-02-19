package com.tg.testui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity


class LaunchActivity : AppCompatActivity() {
    open var mWebview: WebView? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mWebview = findViewById(R.id.myWeb)
//        mWebview?.loadUrl("https://www.reddit.com/")
        mWebview?.loadUrl("https://e-creatoerzw.com/#/pages/views/login/index?isapp=1")
        WebView.setWebContentsDebuggingEnabled(true)
        val settings: WebSettings = mWebview?.settings!!
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        settings.useWideViewPort = true // 关键点

        settings.javaScriptEnabled = true
//        mWebview?.setWebViewClient(myWebViewClient)
        mWebview?.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
                Log.d("test", "onShowCustomView: ++++++++++")
            }

            override fun onHideCustomView() {
                super.onHideCustomView()
                Log.d("test", "onHideCustomView: jjjkljlkjkljkljlkjkl")
            }
        }
//        mWebview?.setWebChromeClient(InsideWebChromeClient())
    }

    private class InsideWebChromeClient : WebChromeClient() {
        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        private val TAG: String = "test"
        override fun onShowCustomView(view: View?, callback: CustomViewCallback) {
            super.onShowCustomView(view, callback)
            Log.d(TAG, "onShowCustomView: +++++++++++++++++++")
            if (mCustomView != null) {
                callback.onCustomViewHidden()
                return
            }
            mCustomView = view
//            mWebview.addView(mCustomView)
            mCustomViewCallback = callback

//            webview.setVisibility(View.GONE)
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }

        override fun onHideCustomView() {
            Log.d(TAG, "onHideCustomView: jjjjjjjjjjjjjjjjjjjjj")
//            mWebView.setVisibility(View.VISIBLE)
//            if (mCustomView == null) {
//                return
//            }
//            mCustomView.setVisibility(View.GONE)
//            mFrameLayout.removeView(mCustomView)
//            mCustomViewCallback!!.onCustomViewHidden()
//            mCustomView = null
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            super.onHideCustomView()
        }
    }
}