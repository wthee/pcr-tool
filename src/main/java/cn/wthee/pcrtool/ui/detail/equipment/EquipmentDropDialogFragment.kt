package cn.wthee.pcrtool.ui.detail.equipment

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentDropAdapter
import cn.wthee.pcrtool.databinding.FragmentEquipmentDropListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ScreenUtil

class EquipmentDropDialogFragment : DialogFragment() {

    companion object {
        private const val EID = "equip_id"

        fun getInstance(equipId: Int): EquipmentDropDialogFragment {
            val fragment = EquipmentDropDialogFragment()
            val bundle = Bundle()
            bundle.putInt(EID, equipId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentEquipmentDropListBinding
    private lateinit var dropsAdapter: EquipmentDropAdapter
    private var equipId: Int = Constants.UNKNOW_EQUIP_ID
    private val viewModel = InjectorUtil.provideEquipmentDetailsViewModelFactory().create(EquipmentDetailsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        equipId = requireArguments().getInt(EID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEquipmentDropListBinding.inflate(inflater, container, false)
        //初始化列表
        LinearLayoutManager(MyApplication.getContext()).also {
            it.orientation = LinearLayoutManager.VERTICAL
            binding.drops.layoutManager = it
        }
        dropsAdapter = EquipmentDropAdapter()
        binding.drops.adapter = dropsAdapter
        binding.back.setOnClickListener {
            dialog?.dismiss()
        }
        //掉落地区
        viewModel.getDropInfos(equipId)
        viewModel.equipDropInfos.observe(viewLifecycleOwner, Observer {
            dropsAdapter.submitList(it)
        })
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
//        window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))

        val params = window?.attributes
        params?.width = ScreenUtil.getWidth()  - 50
        params?.windowAnimations = R.style.PopUpAnimation
        window?.attributes = params
    }

}