package cn.wthee.pcrtool.ui.detail.equipment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentDropAdapter
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsDialogBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.LOG_TAG
import cn.wthee.pcrtool.utils.DrawerUtil
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.InjectorUtil


private const val EQUIP = "equip"


class EquipmentDetailsDialogFragment : DialogFragment() {

    private lateinit var equip: EquipmentData
    private lateinit var binding: FragmentEquipmentDetailsDialogBinding
    private lateinit var dropsAdapter: EquipmentDropAdapter

    companion object {
        fun getInstance(equip: EquipmentData) =
            EquipmentDetailsDialogFragment().apply {
                arguments = Bundle().apply { putSerializable(EQUIP, equip) }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            equip = it.getSerializable(EQUIP) as EquipmentData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentDetailsDialogBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        binding.info.apply {
            //图标
            val picUrl = Constants.EQUIPMENT_URL + equip.equipmentId + Constants.WEPB
            GlideUtil.load(picUrl, itemPic, R.drawable.error, parentFragment)
            //共享元素
            itemPic.transitionName = "pic_${equip.equipmentId}"
            //描述
            desc.text = equip.getDesc()
            //掉落地区
            val lm1 = LinearLayoutManager(MyApplication.getContext())
            lm1.orientation = LinearLayoutManager.VERTICAL
            drops.layoutManager = lm1
            dropsAdapter = EquipmentDropAdapter()
            drops.adapter = dropsAdapter
        }
        //获取掉落信息
        val viewModel = InjectorUtil.provideEquipmentDetailsViewModelFactory()
            .create(EquipmentDetailsViewModel::class.java)
        viewModel.getDropInfos(equip)
        viewModel.equipDropInfos.observe(viewLifecycleOwner, Observer {
            dropsAdapter.submitList(it)
        })
        DrawerUtil.bindAllViewOnTouchListener(binding.root, this, arrayListOf(binding.info.drops))
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        try {
            val win: Window? = dialog!!.window
            win?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val dm = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(dm)

            win?.setWindowAnimations(R.style.BottomDialogAnimation)
            val params = win?.attributes!!
            params.gravity = Gravity.BOTTOM
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = dm.heightPixels / 2
            win.attributes = params
        } catch (e: Exception) {
            Log.e(LOG_TAG, e.message ?: "")
        }

    }
}