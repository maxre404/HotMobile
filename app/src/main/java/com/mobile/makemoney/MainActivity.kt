package com.mobile.makemoney

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {
    protected val GALLERY1REQUESTCODE = 220
    var webView: WebView? = null
    var WEB_URL = BuildConfig.HOST
    var progressBar: ProgressBar? = null
    var TAG = "test"
    var appSchemaList = listOf("whatsapp:", "https://t.me/", "https://www.facebook.com")
    protected var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null
    protected var uploadMessage: ValueCallback<Uri>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWebView()
    }

    fun initWebView() {
        webView = findViewById<WebView>(R.id.myWeb)
        progressBar = findViewById(R.id.progressBar1)
        val webClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                //处理intent协议
                //处理intent协议
                //whatsapp://send/?phone=6283165367506
                //https://t.me/     telegram
                var newUrl = request?.url?.toString()
                Log.d(TAG, "shouldOverrideUrlLoading: $newUrl")
                val find = appSchemaList.find {
                    newUrl?.startsWith(it) == true
                }
                if (null != find) {
                    val intent: Intent
                    try {
                        intent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
                        startActivity(intent)
                        return true
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            this@MainActivity,
                            "whatsapp not installed",
                            Toast.LENGTH_SHORT
                        ).show()
                        return true
                    }
                }
                return false
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
//                super.onReceivedSslError(view, handler, error)
                handler?.proceed()
            }
        }

        //下面这些直接复制就好
        webView?.webViewClient=webClient
        webView?.webChromeClient = object :WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar?.visibility = View.GONE;
                } else {
                    progressBar?.visibility = View.VISIBLE;
                    progressBar?.progress = newProgress;
                }
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                uploadMessageAboveL = filePathCallback
                //调用系统相机或者相册
                //调用系统相机或者相册
                uploadPicture()
                return true
            }

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                val b = AlertDialog.Builder(this@MainActivity)
                b.setMessage(message)
                b.setPositiveButton(
                    android.R.string.ok
                ) { _, _ -> result?.confirm() }
                b.setCancelable(false)
                b.create().show()
                return false
            }
        }

        var webSettings = webView!!.settings
        webSettings.javaScriptEnabled = true  // 开启 JavaScript 交互
//        webSettings.setAppCacheEnabled(true) // 启用或禁用缓存
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
//        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
        webSettings.domStorageEnabled = true // 启用或禁用DOM缓存
//        webSettings.setAppCachePath(cacheDir.path) // 设置应用缓存路径

        // 缩放操作
        webSettings.setSupportZoom(false) // 支持缩放 默认为true 是下面那个的前提
        webSettings.builtInZoomControls = false // 设置内置的缩放控件 若为false 则该WebView不可缩放
        webSettings.displayZoomControls = false // 隐藏原生的缩放控件

        webSettings.blockNetworkImage = false // 禁止或允许WebView从网络上加载图片
        webSettings.loadsImagesAutomatically = true // 支持自动加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.safeBrowsingEnabled = true // 是否开启安全模式
        }

        webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
        webSettings.setSupportMultipleWindows(true) // 设置WebView是否支持多窗口

        // 设置自适应屏幕, 两者合用
        webSettings.useWideViewPort = true  // 将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true  // 缩放至屏幕的大小
        webSettings.allowFileAccess = true // 设置可以访问文件

        webSettings.setGeolocationEnabled(true) // 是否使用地理位置
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE;
        webView?.fitsSystemWindows = true
        webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webView?.loadUrl(WEB_URL)
        Log.d(TAG, "load url: $WEB_URL")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            if (webView!!.canGoBack()) {
                webView!!.goBack()  //返回上一个页面
                return true
            } else {
                finish()
                return true
            }
        }
        return false
    }

    protected fun uploadPicture() {
        val i = Intent(Intent.ACTION_PICK, null)
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(i, GALLERY1REQUESTCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY1REQUESTCODE) {
            if (uploadMessage == null && uploadMessageAboveL == null) {
                return
            }
            //取消拍照或者图片选择时,返回null,否则<input file> 就是没有反应
            if (resultCode != RESULT_OK) {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL!!.onReceiveValue(null)
                    uploadMessageAboveL = null
                }
            } else {
                var imageUri: Uri? = null
                when (requestCode) {
                    GALLERY1REQUESTCODE -> if (data != null) {
                        imageUri = data.data
                    }
                }
                //上传文件
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(imageUri)
                    uploadMessage = null
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL!!.onReceiveValue(arrayOf<Uri>(imageUri!!))
                    uploadMessageAboveL = null
                }
            }
        }
    }
}