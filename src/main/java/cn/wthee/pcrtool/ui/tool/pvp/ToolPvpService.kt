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
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import cn.wthee.pcrtool.MainActivity.Companion.mHeight
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.adapters.PvpCharacterPageAdapter
import cn.wthee.pcrtool.adapters.PvpCharacterResultAdapter
import cn.wthee.pcrtool.data.OnPostListener
import cn.wthee.pcrtool.data.PvpDataRepository
import cn.wthee.pcrtool.data.model.PVPData
import cn.wthee.pcrtool.database.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.FragmentToolPvpFloatWindowBinding
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment.Companion.selects
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import cn.wthee.pcrtool.utils.dp
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Response


class ToolPvpService : Service() {

    companion object {
        const val CHANNEL_ID = "Overlay_notification_channel"
        lateinit var progressBar: ProgressBar
        var isMin = false
        lateinit var activity: AppCompatActivity
        lateinit var selectedAdapter: PvpCharacterAdapter

    }

    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private lateinit var binding: FragmentToolPvpFloatWindowBinding
    private var isMoved = false
    private var character1 = listOf<PvpCharacterData>()
    private var character2 = listOf<PvpCharacterData>()
    private var character3 = listOf<PvpCharacterData>()
    private lateinit var adapter: PvpCharacterResultAdapter

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun onStartCommand(intent: Intent?, flg: Int, startId: Int): Int {
        activity = ActivityUtil.instance.currentActivity!!
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
            height = mHeight
        }
        //加载布局

        binding =
            FragmentToolPvpFloatWindowBinding.inflate(activity.layoutInflater)
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

        //初始化
        loadDefault()
        setPager()


        binding.apply {
            layoutResult.visibility = View.GONE
            //搜索按钮
            search.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    binding.resultContent.pvpResultLoading.visibility = View.VISIBLE
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
                                            adapter = PvpCharacterResultAdapter(activity)
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
            //toolbar
            val toolbar = ToolbarUtil(binding.resultContent.pvpResultToolbar)
            toolbar.title.text = "进攻方信息"
            toolbar.hideRightIcon()
            toolbar.setLeftIcon(R.drawable.ic_back)
            toolbar.setFloatTitle()
            toolbar.leftIcon.setOnClickListener {
                layoutResult.visibility = View.GONE
                adapter.submitList(null)
            }
            //移动按钮//拖动效果，暂时不做
//            var initialX = 0
//            var initialY = 0
//            var initialTouchX = 0f
//            var initialTouchY = 0f
//            move.setOnTouchListener { _, event ->
//                when (event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        initialX = params!!.x
//                        initialY = params!!.y
//                        initialTouchX = event.rawX
//                        initialTouchY = event.rawY
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        val offsetX = (event.rawX - initialTouchX).toInt()
//                        val offsetY = (event.rawY - initialTouchY).toInt()
//                        //移动距离小，视为点击
//                        isMoved = !(abs(offsetX) < 10 && abs(offsetY) < 10)
//
//                        params!!.x = initialX + offsetX
//                        params!!.y = initialY + offsetY
//                        windowManager?.updateViewLayout(binding.root, params)
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        if (!isMoved) {
//                            minWindow()
//                        }
//                    }
//                }
//                return@setOnTouchListener true
//            }
            move.setOnClickListener {
                minWindow()
            }
        }
    }

    //page 初始化
    private fun setPager() {
        binding.pvpPager.offscreenPageLimit = 3
        binding.pvpPager.adapter = PvpCharacterPageAdapter(activity, true)
        TabLayoutMediator(
            binding.tablayoutPosition,
            binding.pvpPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.pos_1)
                }
                1 -> {
                    tab.text = getString(R.string.pos_2)
                }
                2 -> {
                    tab.text = getString(R.string.pos_3)
                }
            }
        }.attach()
    }

    //最大/小化
    private fun minWindow() {
        binding.apply {
            if (isMin) {
                search.visibility = View.VISIBLE
                floatRight.visibility = View.VISIBLE
                params!!.width = WindowManager.LayoutParams.WRAP_CONTENT
                params!!.height = mHeight
            } else {
                search.visibility = View.GONE
                floatRight.visibility = View.GONE
                params!!.width = 48.dp
                params!!.height = 48.dp
            }
            isMin = !isMin
            windowManager?.updateViewLayout(binding.root, params)
        }
    }

    //初始化
    private fun loadDefault() {
        selectedAdapter = PvpCharacterAdapter(true, activity)
        binding.selectCharacters.adapter = selectedAdapter
        selectedAdapter.submitList(selects)
        selectedAdapter.notifyDataSetChanged()

    }
}