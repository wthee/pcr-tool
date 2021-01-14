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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity.Companion.mFloatingWindowHeight
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpCharacterAdapter
import cn.wthee.pcrtool.adapter.PvpCharacterResultAdapter
import cn.wthee.pcrtool.adapter.PvpLikedAdapter
import cn.wthee.pcrtool.adapter.viewpager.PvpCharacterPagerAdapter
import cn.wthee.pcrtool.data.db.dao.PvpDao
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getIds
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater.getRegion
import cn.wthee.pcrtool.databinding.FragmentToolPvpFloatWindowBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpSelectFragment.Companion.selects
import cn.wthee.pcrtool.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 悬浮窗
 */
class PvpService : Service() {

    companion object {
        var isMin = false
        lateinit var selectedAdapter: PvpCharacterAdapter
        lateinit var fabSearch: FloatingActionButton
    }

    private var windowManager: WindowManager? = null
    private var activity: AppCompatActivity? = null
    private var params: WindowManager.LayoutParams? = null
    private lateinit var binding: FragmentToolPvpFloatWindowBinding
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
        activity = ActivityHelper.instance.currentActivity
        character1 = intent?.getSerializableExtra("character1") as List<PvpCharacterData>
        character2 = intent.getSerializableExtra("character2") as List<PvpCharacterData>
        character3 = intent.getSerializableExtra("character3") as List<PvpCharacterData>
        if (mFloatingWindowHeight > 300.dp) mFloatingWindowHeight = 300.dp
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
            height = mFloatingWindowHeight
            x = 6.dp
            y = 36.dp
        }
        //加载布局
        activity?.let {
            binding =
                FragmentToolPvpFloatWindowBinding.inflate(it.layoutInflater)
            initView()
            windowManager!!.addView(binding.root, params)
        }

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
        activity?.let {
            setPager(it)
        }
        fabSearch = binding.search
        setListener()
    }

    private fun setListener() {
        binding.apply {
            //搜索按钮
            fabSearch.setOnClickListener {
                resultContent.progress.visibility = View.VISIBLE
                if (selects.contains(PvpCharacterData(0, 999))) {
                    ToastUtil.short("请选择 5 名角色~")
                } else {
                    //展示查询结果
                    showResult()
                }
            }
            //返回
            back.setOnClickListener {
                if (this@PvpService::job.isLateinit) {
                    if (!job.isCompleted) {
                        job.cancel()
                    }
                }
                //隐藏查询结果页面
                resultContent.root.visibility = View.GONE
                resultContent.pvpNoData.visibility = View.GONE
                adapter.submitList(null)

                //隐藏返回键和收藏页面
                back.visibility = View.GONE
                likedBg.visibility = View.GONE
                liked.setImageResource(R.drawable.ic_loved_line)
                //显示选择页面
                binding.select.visibility = View.VISIBLE
                searchBg.visibility = View.VISIBLE
            }
            //移动
            move.setOnClickListener {
                minWindow()
            }
            //返回应用
            max.setOnClickListener {
                stopForeground(true)
                NotificationUtil.notificationManager.cancelAll()
                onDestroy()
                val appFullName = "cn.wthee.pcrtool"
                val launchIntent = packageManager.getLaunchIntentForPackage(appFullName)
                launchIntent?.let { startActivity(it) }
            }
            //关闭
            max.setOnLongClickListener {
                stopForeground(true)
                NotificationUtil.notificationManager.cancelAll()
                onDestroy()
                return@setOnLongClickListener true
            }
            //收藏
            val dao = AppPvpDatabase.getInstance().getPvpDao()
            //初始化适配器
            val likedAdapter = PvpLikedAdapter(true)
            binding.listLiked.adapter = likedAdapter
            val region = getRegion()
            liked.setOnClickListener {
                if (likedBg.visibility == View.VISIBLE) {
                    searchBg.visibility = View.VISIBLE
                    likedBg.visibility = View.INVISIBLE
                    liked.setImageResource(R.drawable.ic_loved_line)
                } else {
                    liked.setImageResource(R.drawable.ic_loved)
                    searchBg.visibility = View.INVISIBLE
                    likedBg.visibility = View.VISIBLE
                    MainScope().launch {
                        setSwipeDelete(dao, region, likedAdapter)
                    }
                }
            }
        }
    }

    private suspend fun setSwipeDelete(
        dao: PvpDao,
        region: Int,
        likedAdapter: PvpLikedAdapter
    ) {
        val data = dao.getAll(region)
        likedAdapter.submitList(data) {
            updateTip(data)
        }

        //列表设置左右滑动
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @SuppressLint("SimpleDateFormat")
            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                MainScope().launch {
                    val atks =
                        viewHolder.itemView.findViewById<MaterialTextView>(R.id.atk_ids)
                    val defs =
                        viewHolder.itemView.findViewById<MaterialTextView>(R.id.def_ids)
                    val type =
                        viewHolder.itemView.findViewById<MaterialTextView>(R.id.type)
                    val atkIds = atks.text.toString()
                    val defIds = defs.text.toString()
                    val typeInt = type.text.toString().toInt()
                    //删除记录
                    dao.delete(dao.getLiked(atkIds, defIds, region, typeInt)!!)
                    val result = dao.getAll(region)
                    likedAdapter.submitList(result) {
                        updateTip(result)
                    }
                }
            }
        }).attachToRecyclerView(binding.listLiked)
    }

    private fun updateTip(data: List<PvpLikedData>) {
        binding.likeTip.text =
            if (data.isNotEmpty())
                getString(R.string.liked_count, data.size)
            else
                getString(R.string.no_liked_data)
    }

    //展示查询结果
    private fun showResult() {
        binding.apply {
            //刷新已选择列表
            selectedAdapter.apply {
                submitList(selects) {
                    notifyDataSetChanged()
                }
            }
            //显示搜索结果布局
            searchBg.visibility = View.VISIBLE
            likedBg.visibility = View.INVISIBLE
            liked.setImageResource(R.drawable.ic_loved_line)
            select.visibility = View.INVISIBLE
            back.visibility = View.VISIBLE
            resultContent.root.visibility = View.VISIBLE
            //开始查询
            job = MainScope().launch {
                resultContent.pvpNoData.visibility = View.GONE
                val result = MyAPIRepository.getPVPData(selects.getIds())
                if (result.status == 0) {
                    if (result.data!!.isEmpty()) {
                        resultContent.pvpNoData.visibility = View.VISIBLE
                    }
                    //显示结果
                    adapter.submitList(result.data!!.sortedByDescending {
                        it.up
                    })
                } else if (result.status == -1) {
                    ToastUtil.short(result.message)
                    select.visibility = View.VISIBLE
                }
                resultContent.progress.visibility = View.GONE
            }
        }
    }

    //page 初始化
    private fun setPager(activity: AppCompatActivity) {
        binding.pvpPager.offscreenPageLimit = 3
        binding.pvpPager.adapter = PvpCharacterPagerAdapter(activity, true)
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
                params!!.height = mFloatingWindowHeight
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
        selectedAdapter = PvpCharacterAdapter(true)
        binding.selectCharacters.adapter = selectedAdapter
        selectedAdapter.submitList(selects)
        selectedAdapter.notifyDataSetChanged()

        binding.apply {
            //查询结果列表
            adapter = PvpCharacterResultAdapter(true)
            resultContent.pvpResultList.adapter = adapter
            resultContent.root.visibility = View.GONE
        }
    }
}