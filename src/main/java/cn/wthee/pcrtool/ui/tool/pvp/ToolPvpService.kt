package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.ProgressBar
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.OnPostListener
import cn.wthee.pcrtool.data.PvpDataRepository
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.database.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.FragmentToolPvpFloatWindowBinding
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment.Companion.selects
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.dp
import com.google.android.material.tabs.TabLayout
import retrofit2.Response
import kotlin.math.abs


class ToolPvpService() : Service() {

    companion object {
        const val CHANNEL_ID = "Overlay_notification_channel"
        lateinit var progressBar: ProgressBar
        var isMin = false
    }

    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private lateinit var binding: FragmentToolPvpFloatWindowBinding
    private var isMoved = false
    private var character1 = listOf<PvpCharacterData>()
    private var character2 = listOf<PvpCharacterData>()
    private var character3 = listOf<PvpCharacterData>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun onStartCommand(intent: Intent?, flg: Int, startId: Int): Int {
        character1 = intent?.getSerializableExtra("character1") as List<PvpCharacterData>
        character2 = intent.getSerializableExtra("character2") as List<PvpCharacterData>
        character3 = intent.getSerializableExtra("character3") as List<PvpCharacterData>
        //窗口设置
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        params = WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            type = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ->
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else -> WindowManager.LayoutParams.TYPE_TOAST
            }
            gravity = Gravity.TOP or Gravity.START
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        //加载布局
        binding =
            FragmentToolPvpFloatWindowBinding.inflate(LayoutInflater.from(MyApplication.context))
        initView()
        windowManager!!.addView(binding.root, params)

        //前台通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "running",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setSound(null, null)
            notificationManager!!.createNotificationChannel(notificationChannel)
            val builder: Notification.Builder = Notification.Builder(this, CHANNEL_ID)
            builder.setContentTitle("服务运行中...")
            startForeground(1, builder.build())
        }
        return super.onStartCommand(intent, flg, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            windowManager!!.removeView(binding.root)
        } catch (e: Exception) {

        }
    }

    //初始化布局
    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {

        //初始化列表


        //全部角色列表
        val charactersAdapter1 = PvpCharacterAdapter(true)
        binding.icons1.adapter = charactersAdapter1
        charactersAdapter1.submitList(character1)
        val charactersAdapter2 = PvpCharacterAdapter(true)
        binding.icons2.adapter = charactersAdapter2
        charactersAdapter2.submitList(character2)
        val charactersAdapter3 = PvpCharacterAdapter(true)
        binding.icons3.adapter = charactersAdapter3
        charactersAdapter3.submitList(character3)

        binding.apply {
            resultContent.pvpResultToolbar.root.visibility = View.GONE
            layoutResult.visibility = View.GONE
            //搜索按钮
            search.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    binding.layoutResult.visibility = View.VISIBLE
                    PvpDataRepository.getData(object : OnPostListener {
                        override fun success(data: Response<PVPData>) {
                            try {
                                val responseBody = data.body()
                                if (responseBody == null || responseBody.code != 0) {
                                    ToastUtil.short("查询异常，请稍后重试~")
                                } else {
                                    //展示查询结果
                                    binding.resultContent.apply {
                                        if (responseBody.data.result.isEmpty()) {
                                            pvpNoData.visibility = View.VISIBLE
                                        } else {
                                            pvpNoData.visibility = View.GONE
                                            val adapter = PvpCharacterResultAdapter()
                                            list.adapter = adapter
                                            adapter.submitList(responseBody.data.result)
                                        }
                                    }

                                }
                            } catch (e: Exception) {
                                ToastUtil.short("数据解析失败~")
                            }
                            binding.resultContent.pvpResultLoading.visibility = View.GONE
                        }

                        override fun error() {
                            binding.layoutResult.visibility = View.GONE
                            ToastUtil.short("查询失败，请检查网络~")
                        }
                    })
                }
            }
            //关闭搜索结果
            resultClose.setOnClickListener {
                layoutResult.visibility = View.GONE
            }
            //移动按钮
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f
            move.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.x
                        initialY = params!!.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val offsetX = (event.rawX - initialTouchX).toInt()
                        val offsetY = (event.rawY - initialTouchY).toInt()
                        //移动距离小，视为点击
                        isMoved = !(abs(offsetX) < 10 && abs(offsetY) < 10)
                        params!!.x = initialX + offsetX
                        params!!.y = initialY + offsetY
                        windowManager?.updateViewLayout(binding.root, params)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isMoved) {
                            minWindow()
                        }
                    }
                }
                return@setOnTouchListener true
            }
            //tab切换
            tablayoutPosition.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    showList(tab?.position ?: 0)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }

    private fun minWindow() {
        binding.apply {
            if (isMin) {
                search.visibility = View.VISIBLE
                floatRight.visibility = View.VISIBLE
                params!!.width = WindowManager.LayoutParams.WRAP_CONTENT
                params!!.height = WindowManager.LayoutParams.WRAP_CONTENT
            } else {
                search.visibility = View.GONE
                floatRight.visibility = View.GONE
                params!!.width = 48.dp.toInt()
                params!!.height = 48.dp.toInt()
            }
            isMin = !isMin
            windowManager?.updateViewLayout(binding.root, params)
        }
    }

    private fun showList(position: Int) {
        binding.apply {
            when (position) {
                0 -> {
                    icons1.visibility = View.VISIBLE
                    icons2.visibility = View.INVISIBLE
                    icons3.visibility = View.INVISIBLE
                }
                1 -> {
                    icons1.visibility = View.INVISIBLE
                    icons2.visibility = View.VISIBLE
                    icons3.visibility = View.INVISIBLE
                }
                2 -> {
                    icons1.visibility = View.INVISIBLE
                    icons2.visibility = View.INVISIBLE
                    icons3.visibility = View.VISIBLE
                }
            }
        }
    }
}