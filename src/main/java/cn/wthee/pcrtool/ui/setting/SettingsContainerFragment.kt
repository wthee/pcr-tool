package cn.wthee.pcrtool.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.FragmentSettingsContainerBinding
import cn.wthee.pcrtool.utils.ToolbarUtil
import javax.inject.Singleton


@Singleton
class SettingsContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsContainerBinding.inflate(inflater, container, false)
        val toolbar = ToolbarUtil(binding.toolbar)
        toolbar.setLeftIcon(R.drawable.ic_detail_back)
        toolbar.hideRightIcon()
        toolbar.leftIcon.setOnClickListener {
            requireActivity().onBackPressed()
        }

        requireActivity().supportFragmentManager
            .beginTransaction()
            .add(R.id.container, MainSettingsFragment())
            .commit()
        return binding.root
    }

}