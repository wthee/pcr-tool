package cn.wthee.pcrtool.ui.tool.news

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.wthee.pcrtool.databinding.FragmentToolNewsDetailBinding
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ToolNewsDetailFragment : BottomSheetDialogFragment() {
    private val REGION = "region"
    private val NEWSID = "news_id"
    private val URL = "url"

    private var region = 0
    private var newsId = 0
    private var url = ""
    private lateinit var binding: FragmentToolNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            region = it.getInt(REGION)
            newsId = it.getInt(NEWSID)
            url = it.getString(URL) ?: ""
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolNewsDetailBinding.inflate(inflater, container, false)
        binding.apply {
            ToolbarUtil(toolBar).apply {
                setCenterTitle("详情信息")
                leftIcon.setOnClickListener {
                    dialog?.dismiss()
                }
            }
            Log.e("news", url)
            webView.loadUrl(url)
            //设置
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    view.loadUrl(request.url.toString())
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loading.visibility = View.GONE
                }
            }
            webView.settings.apply {
                javaScriptEnabled = true
                useWideViewPort = true //将图片调整到适合webview的大小
                loadWithOverviewMode = true // 缩放至屏幕的大小
            }

            if (region == 2) {
                val params = webView.layoutParams
                params.height = ScreenUtil.getHeight(requireContext())
                webView.layoutParams = params
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(region: Int, newsId: Int, url: String) =
            ToolNewsDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                    putInt(NEWSID, newsId)
                    putString(URL, url)
                }
            }
    }
}