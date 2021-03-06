package cn.wthee.pcrtool.ui.tool.news

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.core.widget.NestedScrollView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentToolNewsDetailBinding
import cn.wthee.pcrtool.ui.common.CommonBottomSheetDialogFragment
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants.REGION


private const val NEWSID = "news_id"
private const val URL = "url"

/**
 * 公告详情
 *
 * 根据 [url] 加载公告
 *
 * 页面布局 [FragmentToolNewsDetailBinding]
 *
 * ViewModels []
 */
class NewsDetailDialogFragment : CommonBottomSheetDialogFragment() {

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
        initWebview()
        setListener()
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebview() {
        binding.apply {
            if (region == 4) webView.visibility = View.VISIBLE
            //设置
            webView.settings.apply {
                domStorageEnabled = true
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                useWideViewPort = true //将图片调整到适合webView的大小
                loadWithOverviewMode = true // 缩放至屏幕的大小
                javaScriptCanOpenWindowsAutomatically = true
                loadsImagesAutomatically = false
                blockNetworkImage = true
            }
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
                    webView.settings.apply {
                        loadsImagesAutomatically = true
                        blockNetworkImage = false
                    }
                    if (region == 2) {
                        //取消内部滑动
                        webView.loadUrl(
                            """
                                javascript:
                                $('#news-content').css('overflow','inherit');
                                $('.news-detail').css('top','0.3rem');
                                $('.top').css('display','none');
                                $('.title').css('font-size','16px');
                                $('.title').css('color','#2c94e4');
                                $('#news-content').css('margin-bottom','1rem');
                            """.trimIndent()
                        )
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
                                $('h3').css('font-size','16px');
                                $('.news_con').css('margin','0px');
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
                        )
                    }
                    loading.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }
            }
            //加载网页
            webView.loadUrl(url)
        }
    }

    private fun setListener() {
        binding.apply {
            fabBrowser.setOnClickListener {
                BrowserUtil.open(requireContext(), url)
            }
            fabTop.setImageResource(R.drawable.ic_left)
            fabTop.setOnClickListener {
                dialog?.dismiss()
            }
            scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                if (scrollView.canScrollVertically(-1)) {
                    fabTop.setImageResource(R.drawable.ic_top)
                    fabTop.setOnClickListener {
                        scrollView.smoothScrollTo(0, 0)
                    }
                } else {
                    fabTop.setImageResource(R.drawable.ic_left)
                    fabTop.setOnClickListener {
                        dialog?.dismiss()
                    }
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(region: Int, newsId: Int, url: String) =
            NewsDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(REGION, region)
                    putInt(NEWSID, newsId)
                    putString(URL, url)
                }
            }
    }
}