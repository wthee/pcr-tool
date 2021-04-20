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
import cn.wthee.pcrtool.data.entity.PvpLikedData
import cn.wthee.pcrtool.data.view.PvpCharacterData
import cn.wthee.pcrtool.data.view.getDefault
import cn.wthee.pcrtool.data.view.getIdStr
import cn.wthee.pcrtool.database.AppPvpDatabase
import cn.wthee.pcrtool.database.DatabaseUpdater.getRegion
import cn.wthee.pcrtool.databinding.FragmentToolPvpLikedCustomizeBinding
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.ToolbarHelper
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 竞技场信息自定义
 *
 * 页面布局 [FragmentToolPvpLikedCustomizeBinding]
 *
 * ViewModels []
 */
class PvpLikedSelectFragment : Fragment() {

    companion object {
        var atkSelected = getDefault()
        var defSelected = getDefault()
    }

    private lateinit var binding: FragmentToolPvpLikedCustomizeBinding
    private var customize = 0

    private var atks = ""
    private var defs = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 500L
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        atkSelected = getDefault()
        defSelected = getDefault()
        atks = ""
        defs = ""
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolPvpLikedCustomizeBinding.inflate(layoutInflater, container, false)
        binding.root.transitionName = "liked_add"
        initAtkPage()
        val empty =
            PvpCharacterData(
                0,
                999
            )
        binding.pvpNext.setOnClickListener {
            if (customize == 0) {
                //选择防守队伍
                if (!PvpIconFragment.selects.contains(empty)) {
                    atkSelected = PvpIconFragment.selects
                    atks = atkSelected.getIdStr()
                    initDefPage()
                    customize = 1
                } else {
                    ToastUtil.short("请选择 5 名角色~")
                }
            } else if (customize == 1) {
                //选择防守
                //保存
                if (!PvpIconFragment.selects.contains(empty)) {
                    defs = defSelected.getIdStr()
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
                        PvpIconFragment.selects = getDefault()
                        findNavController().navigateUp()
                    }
                } else {
                    ToastUtil.short("请选择 5 名角色~")
                }
            }
        }
        //返回进攻方选择
        binding.pvpPre.setOnClickListener {
            //返回进攻选择
            customize = 0
            defSelected = PvpIconFragment.selects
            initAtkPage()
        }
        return binding.root
    }

    private fun initDefPage() {
        //获取防守队伍
        PvpIconFragment.selectedAdapter.submitList(defSelected)
        childFragmentManager.beginTransaction()
            .replace(R.id.layout_select, PvpIconFragment(1))
            .commit()
        binding.pvpNext.icon = ResourcesUtil.getDrawable(R.drawable.ic_loved)
        binding.pvpNext.text = "保存"
        binding.pvpPre.show()
        ToolbarHelper(binding.toolPvp).setMainToolbar(
            R.drawable.ic_def,
            "请添加防守方队伍信息"
        )

    }

    private fun initAtkPage() {
        //获取进攻队伍
        PvpIconFragment.selectedAdapter.submitList(atkSelected)
        binding.pvpNext.icon = ResourcesUtil.getDrawable(R.drawable.ic_def)
        binding.pvpNext.text = "防守方"
        binding.pvpPre.hide()
        childFragmentManager.beginTransaction()
            .replace(R.id.layout_select, PvpIconFragment(0))
            .commit()
        //设置头部
        ToolbarHelper(binding.toolPvp).setMainToolbar(
            R.drawable.ic_pvp,
            "请添加进攻方队伍信息"
        )
    }
}