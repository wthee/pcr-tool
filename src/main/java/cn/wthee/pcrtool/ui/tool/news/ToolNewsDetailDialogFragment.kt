package cn.wthee.pcrtool.ui.tool.news

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import cn.wthee.pcrtool.databinding.FragmentToolNewsDetailBinding
import cn.wthee.pcrtool.utils.BrowserUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ToolNewsDetailDialogFragment : BottomSheetDialogFragment() {
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
    ): View {
        binding = FragmentToolNewsDetailBinding.inflate(inflater, container, false)
        binding.apply {
            Log.e("news", url)

            openBrowse.setOnClickListener {
//                ClipboardUtli.add(url)
                BrowserUtil.open(requireContext(), url)
            }

            //设置
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    view.loadUrl(url!!)
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
                    if (region == 2) {
                        //取消内部滑动
                        webView.loadUrl(
                            """
                            javascript:
                            $('#news-content').css('overflow','inherit');
                            $('.news-detail').css('top','0.3rem');
                            $('.top').css('display','none');
                        """.trimIndent()
                        );
                    }
                    if (region == 3) {
                        webView.loadUrl(
                            """
                            javascript:
                            $('.menu').css('display','none');
                            $('.story_container_m').css('display','none');                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
                            $('.title').css('display','none');
                            $('header').css('display','none');
                            $('footer').css('display','none');
                            $('aside').css('display','none');
                            $('.paging').css('display','none');
                        """.trimIndent()
                        )
                    }
                    if (region == 4) {
                        webView.loadUrl(
                            """
                            javascript:
                            $('#main_area').css('display','none');
                            $('.bg-gray').css('display','none');
                            $('.news_prev').css('display','none');
                            $('.news_next').css('display','none');
                            $('header').css('display','none');
                            $('footer').css('display','none');
                        """.trimIndent()
                        );
                    }
                    loading.visibility = View.GONE
                    tip.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }
            }
            webView.settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
                useWideViewPort = true //将图片调整到适合webview的大小
                loadWithOverviewMode = true // 缩放至屏幕的大小
                javaScriptCanOpenWindowsAutomatically = true
                if (region == 3 || region == 4) {
                    tip.visibility = View.VISIBLE
                    loadsImagesAutomatically = false
                }
            }
            //加载网页
            webView.loadUrl(url)
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(region: Int, newsId: Int, url: String) =
            ToolNewsDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                    putInt(NEWSID, newsId)
                    putString(URL, url)
                }
            }
    }
}