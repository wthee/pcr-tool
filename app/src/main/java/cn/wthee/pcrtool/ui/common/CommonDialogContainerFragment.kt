package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentContainerBinding
import cn.wthee.pcrtool.ui.skill.SkillFragment
import cn.wthee.pcrtool.utils.Constants

/**
 * 底部弹窗基类
 */
class CommonDialogContainerFragment : CommonBottomSheetDialogFragment() {

    private val FRAGMENT_TYPE = "fragment_type"
    private var type = 0
    private var uid = 0
    private var skillType = 0

    companion object {
        fun loadSkillFragment(uid: Int, skillType: Int) =
            CommonDialogContainerFragment().apply {
                arguments = Bundle().apply {
                    putInt(FRAGMENT_TYPE, 1)
                    putInt(Constants.UID, uid)
                    putInt(Constants.TYPE_SKILL, skillType)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt(FRAGMENT_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentContainerBinding.inflate(inflater, container, false)
        when (type) {
            1 -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.container, SkillFragment.getInstance(uid, skillType))
                    .commit()
            }
            else -> {
            }

        }

        return binding.root
    }
}