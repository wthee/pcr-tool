package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapter.PvpLikedAdapter
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.databinding.FragmentToolPvpLikedBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.RecyclerViewHelper.setScrollToTopListener
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PvpLikedFragment : Fragment() {

    private lateinit var binding: FragmentToolPvpLikedBinding

    private val viewModel =
        InjectorUtil.providePvpViewModelFactory().create(PvpLikedViewModel::class.java)
    private var region = DatabaseUpdater.getRegion()
    private lateinit var adapter: PvpLikedAdapter
    private var allData = arrayListOf<PvpLikedData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //过渡
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(Color.TRANSPARENT)
        }
        exitTransition = Hold()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpLikedBinding.inflate(inflater, container, false)
        init()
        setListener(adapter)
        viewModel.data.observe(viewLifecycleOwner, Observer {
            allData.clear()
            allData.addAll(it)
            adapter.submitList(it) {
                updateTip(it)
            }
            startPostponedEnterTransition()
        })
        ToolbarUtil(binding.toolPvpLike).setToolHead(
            R.drawable.ic_loved_line,
            "收藏信息"
        )
        if (savedInstanceState == null) {
            postponeEnterTransition()
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun updateTip(it: List<PvpLikedData>) {
        binding.pvpAll.text = getString(R.string.tab_all) + " " + it.size
        binding.pvpCus.text = getString(R.string.customize) + " " + it.filter {
            it.type == 1
        }.size
    }

    private fun init() {
        binding.root.transitionName = "liked"
        binding.fabAdd.transitionName = "liked_add"
        FabHelper.addBackFab(2)
        //获取数据版本
        viewModel.getLiked(region)
        //初始化适配器
        adapter = PvpLikedAdapter(false)
        binding.listLiked.adapter = adapter
    }

    private fun setListener(adapter: PvpLikedAdapter) {
        //自定义
        binding.fabAdd.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("customize", 0)
            }
            val extras = FragmentNavigatorExtras(
                it to it.transitionName
            )
            findNavController().navigate(
                R.id.action_pvpLikedFragment_to_pvpLikedCustomize, bundle,
                null, extras
            )
        }
        //切换
        binding.pvpLikeChips.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.pvp_all -> {
                    adapter.submitList(allData) {
                        updateTip(allData)
                    }
                }
                R.id.pvp_cus -> {
                    val cus = allData.filter { it.type == 1 }
                    adapter.submitList(cus) {
                        updateTip(cus)
                    }
                }
            }
        }
        //列表设置左右滑动
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            @SuppressLint("SimpleDateFormat")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                lifecycleScope.launch {
                    val dao = AppPvpDatabase.getInstance().getPvpDao()
                    val atks = viewHolder.itemView.findViewById<MaterialTextView>(R.id.atk_ids)
                    val defs = viewHolder.itemView.findViewById<MaterialTextView>(R.id.def_ids)
                    val type = viewHolder.itemView.findViewById<MaterialTextView>(R.id.type)
                    val atkIds = atks.text.toString()
                    val defIds = defs.text.toString()
                    val typeInt = type.text.toString().toInt()
                    val data = dao.getLiked(atkIds, defIds, region, typeInt)!!
                    //删除记录
                    dao.delete(data)
                    val result = dao.getAll(region)
                    adapter.submitList(result) {
                        updateTip(result)
                    }
                    //显示撤回
                    Snackbar.make(binding.root, "已取消收藏~", Snackbar.LENGTH_LONG)
                        .setAction("撤回") {
                            //添加记录
                            lifecycleScope.launch {
                                dao.insert(data)
                                val list = dao.getAll(region)
                                val position = list.indexOf(data)
                                adapter.submitList(list) {
                                    updateTip(list)
                                }
                                delay(500L)
                                binding.listLiked.smoothScrollToPosition(position)
                            }
                        }
                        .show()
                }
            }
        }).attachToRecyclerView(binding.listLiked)
        //滚动监听
        binding.listLiked.setScrollToTopListener(binding.fabTop)

    }


}