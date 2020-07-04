package cn.wthee.pcrtool.ui.detail.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentDropAdapter
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.InjectorUtil


private const val EQUIP = "equip"


class EquipmentDetailsFragment : Fragment() {

    private lateinit var equip: EquipmentData
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var dropsAdapter: EquipmentDropAdapter

    companion object {
        fun getInstance(equip: EquipmentData) =
            EquipmentDetailsFragment().apply {
                arguments = Bundle().apply { putSerializable(EQUIP, equip) }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            equip = it.getSerializable(EQUIP) as EquipmentData
        }
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentDetailsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        //获取掉落信息
        val viewModel = InjectorUtil.provideEquipmentDetailsViewModelFactory()
            .create(EquipmentDetailsViewModel::class.java)
        viewModel.getDropInfos(equip)
        binding.apply {
            //toolbar
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            toolbar.title = equip.equipmentName
            //共享元素
            info.itemPic.transitionName = "pic_${equip.equipmentId}"
            //图标
            val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEPB
            GlideUtil.load(picUrl, info.itemPic, R.drawable.error, parentFragment)
            //描述
            info.desc.text = equip.getDesc()
            //属性词条
            val lm = object : LinearLayoutManager(MyApplication.getContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            lm.orientation = LinearLayoutManager.VERTICAL
            info.attrs.layoutManager = lm
            val adapter = EquipmentAttrAdapter()
            info.attrs.adapter = adapter
            adapter.submitList(equip.getAttrs())
            //掉落地区
            val lm1 = LinearLayoutManager(MyApplication.getContext())
            lm1.orientation = LinearLayoutManager.VERTICAL
            info.drops.layoutManager = lm1
            dropsAdapter = EquipmentDropAdapter()
            info.drops.adapter = dropsAdapter
        }
        viewModel.equipDropInfos.observe(viewLifecycleOwner, Observer {
            dropsAdapter.submitList(it)
        })
        return binding.root
    }

}