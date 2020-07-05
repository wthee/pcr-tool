package cn.wthee.pcrtool.ui.detail.equipment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsDialogBinding
import cn.wthee.pcrtool.utils.Constants.LOG_TAG


private const val EQUIP = "equip"


class EquipmentDetailsDialogFragment : DialogFragment() {

    private lateinit var equip: EquipmentData
    private lateinit var binding: FragmentEquipmentDetailsDialogBinding

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
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.dialog_container, EquipmentDetailsFragment.getInstance(equip))
            .commit()
//        DrawerUtil.bindAllViewOnTouchListener(binding.root, this, arrayListOf(binding.dialogContainer))
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