package com.example.obs.player.ui.widget

import android.webkit.*
import com.mobile.makemoney.App
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


open class FileCacheWebViewClient(val gameId: String) : WebViewClient() {
    var fileSuffixArray = listOf<String>(".png", ".js", "atlas", ".json")

    init {
        var file = File("${App.getInstance().filesDir}/Game/$gameId/")
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        request?.url?.toString()?.let { gameUrl ->
            // 判断本地文件是否存在
            fileSuffixArray.find { suffix ->
                gameUrl.endsWith(suffix)
            }?.let {
                if (isLocalFileExists(gameUrl)) {
                    // 加载本地文件
                    return loadLocalFile(gameUrl);
                } else {
                    // 下载文件并加载
                    return downloadAndLoadFile(gameUrl);
                }
            }
        }
        return super.shouldInterceptRequest(view, request)
    }

    // 判断本地文件是否存在
    private fun isLocalFileExists(url: String): Boolean {
        val file = File(getLocalFilePath(url))
        return file.exists()
    }

    // 获取本地文件路径
    private fun getLocalFilePath(url: String): String? {
        // 在这里根据 URL 获取本地文件的路径
        // 例如，可以将 URL 转换为本地文件路径

        // 示例代码：
        val fileName = url.substring(url.lastIndexOf('/') + 1)
        return "${App.getInstance().filesDir}/Game/$gameId/$fileName"
    }

    // 加载本地文件
    private fun loadLocalFile(url: String): WebResourceResponse? {
        // 在这里实现加载本地文件的逻辑
        // 例如，可以根据文件的路径或其他方式获取文件的输入流
        // 并返回该文件的 WebResourceResponse

        // 示例代码：
        try {
            val inputStream: InputStream = FileInputStream(getLocalFilePath(url))
            val mimeType = getMimeType(url)!!
            return WebResourceResponse(mimeType, "UTF-8", inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // 下载文件并加载
    private fun downloadAndLoadFile(url: String): WebResourceResponse? {
        // 在这里实现下载文件并加载的逻辑
        // 例如，可以使用 HttpURLConnection 下载文件，并保存到本地
        // 然后根据本地文件路径创建 WebResourceResponse 并返回
        // 示例代码：
        try {
            val fileUrl = URL(url)
            val connection: HttpURLConnection = fileUrl.openConnection() as HttpURLConnection
            connection.connect()

            // 获取文件输入流
            val inputStream: InputStream = connection.inputStream

            // 保存文件到本地
            val localFilePath = getLocalFilePath(url)
            saveFile(inputStream, localFilePath!!)

            // 加载本地文件
            val mimeType = getMimeType(url)!!
            return WebResourceResponse(mimeType, "UTF-8", FileInputStream(localFilePath))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // 保存文件到本地
    private fun saveFile(inputStream: InputStream, filePath: String) {
        val file = File(filePath)
        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.close()
        inputStream.close()
    }

    private fun getMimeType(url: String): String? {
        val extension: String = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType: String? = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        return mimeType ?: "application/octet-stream"
    }


}