package cn.wthee.pcrtool.ui.tool.pvp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.db.view.getDefault
import cn.wthee.pcrtool.data.db.view.getIdStr
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater.getRegion
import cn.wthee.pcrtool.databinding.FragmentToolPvpLikedCustomizeBinding
import cn.wthee.pcrtool.utils.FabHelper
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PvpLikedCusFragment : Fragment() {

    private lateinit var binding: FragmentToolPvpLikedCustomizeBinding
    private var customize = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customize = requireArguments().getInt("customize")
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(R.integer.fragment_anim).toLong()
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FabHelper.addBackFab(2)
        binding = FragmentToolPvpLikedCustomizeBinding.inflate(layoutInflater, container, false)
        binding.root.transitionName = "liked_add"
        childFragmentManager.beginTransaction()
            .replace(R.id.layout_select, PvpSelectFragment(customize))
            .commit()
        //设置头部
        ToolbarUtil(binding.toolPvp).setToolHead(
            R.drawable.ic_pvp,
            "请添加进攻方队伍信息"
        )
        binding.pvpNext.setOnClickListener {
            //选择防守队伍
            val empty =
                PvpCharacterData(
                    0,
                    999
                )
            if (!PvpSelectFragment.selects.contains(empty)) {
                val atks = PvpSelectFragment.selects.getIdStr()
                PvpSelectFragment.selects = getDefault()
                PvpSelectFragment.selectedAdapter.submitList(PvpSelectFragment.selects)
                childFragmentManager.beginTransaction()
                    .replace(R.id.layout_select, PvpSelectFragment(1))
                    .commit()
                binding.pvpNext.icon = ResourcesUtil.getDrawable(R.drawable.ic_loved)
                binding.pvpNext.text = "保存"
                ToolbarUtil(binding.toolPvp).setToolHead(
                    R.drawable.ic_def,
                    "请添加防守方队伍信息"
                )
                //保存
                binding.pvpNext.setOnClickListener {
                    if (!PvpSelectFragment.selects.contains(empty)) {
                        val defs = PvpSelectFragment.selects.getIdStr()
                        val date =
                            SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(Date(System.currentTimeMillis()))
                        val cus = PvpLikedData(
                            UUID.randomUUID().toString(),
                            atks,
                            defs,
                            date,
                            getRegion(),
                            1
                        )
                        lifecycleScope.launch {
                            AppPvpDatabase.getInstance().getPvpDao().insert(cus)
                            PvpSelectFragment.selects = getDefault()
                            findNavController().navigateUp()
                        }
                    } else {
                        ToastUtil.short("请选择 5 名角色~")
                    }
                }
            } else {
                ToastUtil.short("请选择 5 名角色~")
            }
        }
        return binding.root
    }
}