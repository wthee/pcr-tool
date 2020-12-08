package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentCommonBottomSheetBinding
import cn.wthee.pcrtool.enums.PageType
import cn.wthee.pcrtool.ui.detail.character.skill.CharacterSkillFragment
import cn.wthee.pcrtool.utils.Constants.UID

/**
 * 底部弹窗容器
 */
class ContainerFragment :
    CommonBasicDialogFragment() {

    private val DIALOG = "dialog"

    companion object {
        fun getInstance(uid: Int, page: PageType) =
            ContainerFragment().apply {
                arguments = Bundle().apply {
                    putInt(UID, uid)
                    putSerializable(DIALOG, page)
                }
            }
    }

    private lateinit var binding: FragmentCommonBottomSheetBinding
    private var uid = 0
    private var page = PageType.CAHRACTER_SKILL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            uid = getInt(UID)
            page = getSerializable(DIALOG) as PageType
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommonBottomSheetBinding.inflate(inflater, container, false)
        when (page) {
            PageType.CAHRACTER_SKILL -> {
                childFragmentManager.beginTransaction()
                    .replace(R.id.container, CharacterSkillFragment.getInstance(uid, true))
                    .commit()
            }
        }
        return binding.root
    }
}