package cn.wthee.pcrtool.ui.tool.enemy

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.entity.EnemyData
import cn.wthee.pcrtool.databinding.FragmentEnemyDetailsBinding
import cn.wthee.pcrtool.ui.common.CommonBasicDialogFragment
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ToolbarUtil
import coil.load
import com.google.android.material.bottomsheet.BottomSheetBehavior

class EnemyDialogFragment : CommonBasicDialogFragment() {

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
            //加载图片
            val picUrl = if (enemy.unit_id < 600101) {
                Constants.UNIT_ICON_URL + enemy.prefab_id
            } else {
                Constants.UNIT_ICON_SHADOW_URL + enemy.getTruePrefabId()
            } + Constants.WEBP
            itemPic.load(picUrl) {
                error(R.drawable.unknow_gray)
            }
            //toolbar
            ToolbarUtil(toolbar).setCenterTitle(enemy.unit_name)
                .leftIcon.setOnClickListener {
                    dialog?.dismiss()
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