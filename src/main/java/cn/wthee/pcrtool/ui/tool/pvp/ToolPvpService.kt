package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
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
import cn.wthee.pcrtool.data.MyAPIRepository
import cn.wthee.pcrtool.data.view.PvpCharacterData
import cn.wthee.pcrtool.databinding.FragmentToolPvpFloatWindowBinding
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment.Companion.selects
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.NotificationUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.dp
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolPvpService : Service() {

    companion object {
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
    private lateinit var job: Job


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    override fun onStartCommand(intent: Intent?, flg: Int, startId: Int): Int {
        activity = ActivityUtil.instance.currentActivity!!
        character1 = intent?.getSerializableExtra("character1") as List<PvpCharacterData>
        character2 = intent.getSerializableExtra("character2") as List<PvpCharacterData>
        character3 = intent.getSerializableExtra("character3") as List<PvpCharacterData>
        if (mHeight > 300.dp) mHeight = 300.dp
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
            y = 36.dp
        }
        //加载布局

        binding =
            FragmentToolPvpFloatWindowBinding.inflate(activity.layoutInflater)
        initView()
        windowManager!!.addView(binding.root, params)

        //前台通知
        NotificationUtil.createForeground(this, "竞技场查询服务运行中...")
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
            //查询结果列表
            adapter = PvpCharacterResultAdapter(activity)
            resultContent.list.adapter = adapter

            resultContent.root.visibility = View.GONE
            resultContent.pvpResultToolbar.root.visibility = View.GONE
            //搜索按钮
            search.setOnClickListener {
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    back.visibility = View.VISIBLE
                    resultContent.loading.root.visibility = View.VISIBLE
                    resultContent.root.visibility = View.VISIBLE
                    job = MainScope().launch {
                        resultContent.pvpNoData.visibility = View.GONE
                        val data = MyAPIRepository.getPVPData()
                        if (data.isEmpty()) {
                            resultContent.pvpNoData.visibility = View.VISIBLE
                            resultContent.root.visibility = View.GONE
                        } else {
                            adapter.submitList(data.sortedByDescending {
                                it.up
                            })
                        }
                        resultContent.loading.root.visibility = View.GONE
                    }
                }
            }
            //关闭搜索结果
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
            //返回
            back.setOnClickListener {
                if (this@ToolPvpService::job.isLateinit) {
                    if (!job.isCancelled) {
                        job.cancel()
                    }
                }
                if (resultContent.root.visibility == View.VISIBLE) {
                    resultContent.root.visibility = View.GONE
                    adapter.submitList(null)
                }
                resultContent.pvpNoData.visibility = View.GONE
                resultContent.loading.root.visibility = View.GONE
                back.visibility = View.GONE
            }
            //移动
            move.setOnClickListener {
                minWindow()
            }
            //关闭
            close.setOnClickListener {
                stopForeground(true)
                NotificationUtil.notificationManager.cancelAll()
                onDestroy()
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