package cn.wthee.pcrtool.ui.detail.equipment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.transition.TransitionInflater
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentAttrAdapter
import cn.wthee.pcrtool.adapters.EquipmentMaterialAdapter
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.InjectorUtil


private const val EQUIP = "equip"


class EquipmentDetailsFragment : Fragment() {

    private lateinit var equip: EquipmentData
    private lateinit var binding: FragmentEquipmentDetailsBinding
    private lateinit var materialAdapter: EquipmentMaterialAdapter

    companion object {
        fun getInstance(equip: EquipmentData) =
            EquipmentDetailsFragment().apply {
                arguments = Bundle().apply { putSerializable(EQUIP, equip) }
            }
    }

    private val viewModel = InjectorUtil.provideEquipmentDetailsViewModelFactory()
        .create(EquipmentDetailsViewModel::class.java)

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
        init()
        setObserve()
        viewModel.getEquipInfos(equip)
        return binding.root
    }

    private fun init() {
        binding.apply {
            //toolbar
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
            toolbar.title = equip.equipmentName
            //共享元素
            itemPic.transitionName = "pic_${equip.equipmentId}"
            //图标
            val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEBP
            GlideUtil.load(picUrl, itemPic, R.drawable.error, parentFragment)
            //描述
            desc.text = equip.getDesc()
            //属性词条
            val lm = object : LinearLayoutManager(MyApplication.getContext()) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }.also {
                it.orientation = LinearLayoutManager.VERTICAL
                attrs.layoutManager = it
            }
            val adapter = EquipmentAttrAdapter()
            attrs.adapter = adapter
            adapter.submitList(equip.getAttrs())
            //合成素材
            materialAdapter = EquipmentMaterialAdapter()
            PagerSnapHelper().attachToRecyclerView(material)
            material.adapter = materialAdapter
        }
    }

    private fun setObserve() {
        viewModel.equipMaterialInfos.observe(viewLifecycleOwner, Observer {
            binding.titleMaterial.text = resources.getString(R.string.material, it.size)
            materialAdapter.submitList(it)
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.equipProgressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

}