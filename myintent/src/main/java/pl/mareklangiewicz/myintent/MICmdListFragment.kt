package pl.mareklangiewicz.myintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.mi_cmd_list_fragment.*
import pl.mareklangiewicz.myfragments.MyFragment

/**
 * Created by Marek Langiewicz on 12.11.15.
 */
open class MICmdListFragment : MyFragment() {

    private val adapter = CmdAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState) // just for logging
        return inflater.inflate(R.layout.mi_cmd_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mi_cmd_recycler_view.setHasFixedSize(true)
        mi_cmd_recycler_view.adapter = adapter
    }

    var imageRes: Int
        get() = adapter.imageRes
        set(value) { adapter.imageRes = value }

    var commands: List<String>
        get() = adapter.commands
        set(commands) { adapter.commands = commands }

    override fun onDestroyView() {
        mi_cmd_recycler_view.adapter = null
        super.onDestroyView()
    }
}

