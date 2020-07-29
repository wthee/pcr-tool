package cn.wthee.pcrtool.ui.detail.enemy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.adapters.EquipmentDropAdapter
import cn.wthee.pcrtool.data.model.entity.EnemyData
import cn.wthee.pcrtool.databinding.FragmentEnemyDetailsBinding
import cn.wthee.pcrtool.databinding.FragmentEquipmentDetailsBinding
import cn.wthee.pcrtool.databinding.FragmentEquipmentDropListBinding
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FabHelper.goBack
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.InjectorUtil
import cn.wthee.pcrtool.utils.ToolbarUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EnemyDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ENEMY = "enemy"

        fun getInstance(enemyData: EnemyData): EnemyDialogFragment {
            val fragment = EnemyDialogFragment()
            val bundle = Bundle()
            bundle.putSerializable(ENEMY, enemyData)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentEnemyDetailsBinding
    private lateinit var enemy: EnemyData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enemy = requireArguments().getSerializable(ENEMY) as EnemyData
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnemyDetailsBinding.inflate(inflater, container, false)
        binding.apply {
            desc.text = enemy.getFixedComment()
            atkTime.text = enemy.normal_atk_cast_time.toString()
            atkArea.text = enemy.search_area_width.toString()
            //图标
            val picUrl = Constants.UNIT_ICON_URL + enemy.unit_id + Constants.WEBP
            GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
            //toolbar
            val cusToolbar = ToolbarUtil(toolbar)
            cusToolbar.apply {
                setLeftIcon(R.drawable.ic_back)
                hideRightIcon()
                setTitleColor(R.color.colorPrimary)
                setBackground(R.color.colorWhite)
                setTitleCenter()
                title.text = enemy.unit_name
                leftIcon.setOnClickListener {
                    dialog?.dismiss()
                }
            }
        }
        return binding.root
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        val v: View = FragmentEnemyDetailsBinding.inflate(layoutInflater).root
        dialog.setContentView(v)

        val layoutParams = (v.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior as BottomSheetBehavior<View>
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}