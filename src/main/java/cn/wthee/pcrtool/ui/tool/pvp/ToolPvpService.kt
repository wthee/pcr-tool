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
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.PvpCharacterAdapter
import cn.wthee.pcrtool.adapters.PvpCharacterPageAdapter
import cn.wthee.pcrtool.database.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.FragmentToolPvpFloatWindowBinding
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment.Companion.selects
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.dp
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs


class ToolPvpService : Service() {

    companion object {
        const val CHANNEL_ID = "Overlay_notification_channel"
        lateinit var selectedAdapter: PvpCharacterAdapter
        lateinit var progressBar: ProgressBar
        var isMin = false
    }

    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null
    private lateinit var binding: FragmentToolPvpFloatWindowBinding
    private lateinit var activity: AppCompatActivity
    private var isMoved = false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flg: Int, startId: Int): Int {
        //窗口设置
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        params = WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            @Suppress("DEPRECATION")
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
        activity = ActivityUtil.instance.currentActivity!!
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

        selectedAdapter = PvpCharacterAdapter(true)
        binding.selectCharacters.adapter = selectedAdapter
        selectedAdapter.submitList(selects)
        progressBar = binding.pvpProgressBar


        binding.apply {
            //搜索按钮
            search.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    ToolPvpResultDialogFragment().show(
                        activity.supportFragmentManager,
                        "pvp"
                    )
                }
            }
            //移动按钮
            var initialX = 0
            var initialY = 0
            var initialTouchX = 0f
            var initialTouchY = 0f
            move.setOnTouchListener { v, event ->
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
                    MotionEvent.ACTION_UP ->{
                        if (!isMoved){
                            minWindow()
                        }
                    }
                }
                return@setOnTouchListener true
            }


            //加载pager
            pvpPager.offscreenPageLimit = 3
            pvpPager.adapter = PvpCharacterPageAdapter(activity, true)
            TabLayoutMediator(
                tablayoutPosition,
                pvpPager
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

}