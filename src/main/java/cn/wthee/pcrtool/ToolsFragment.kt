package cn.wthee.pcrtool

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cn.wthee.pcrtool.databinding.FragmentToolsBinding
import cn.wthee.pcrtool.utils.FabHelper

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ToolsFragment : Fragment() {

    private lateinit var binding: FragmentToolsBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolsBinding.inflate(inflater, container, false)
        //添加返回fab
        FabHelper.addBackFab()
        return binding.root
    }

}