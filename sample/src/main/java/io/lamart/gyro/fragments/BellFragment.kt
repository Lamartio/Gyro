package io.lamart.gyro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import io.lamart.gyro.*
import io.lamart.gyro.actions.Actions
import io.lamart.gyro.utils.Component
import io.lamart.gyro.utils.component

class BellFragment : Fragment(), Component {

    override val state: LiveData<State> by component { state }
    override val actions: Actions by component { actions }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.bell, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bellDescription = view.findViewById<TextView>(R.id.bellDescription)
        val startRinging = view.findViewById<Button>(R.id.startRinging)
        val stopRinging = view.findViewById<Button>(R.id.stopRinging)

        startRinging.setOnClickListener { actions.startRinging() }
        stopRinging.setOnClickListener { actions.stopRinging() }

        state
            .map { state -> state.house.door.bell.description }
            .distinctUntilChanged()
            .observe(this, Observer { bellDescription.text = it })
        state
            .map { state -> !state.house.door.isOpen && !state.house.door.bell.isRinging }
            .distinctUntilChanged()
            .observe(this, Observer { startRinging.isEnabled = it })
        state
            .map { state -> state.house.door.bell.isRinging }
            .distinctUntilChanged()
            .observe(this, Observer { stopRinging.isEnabled = it })
    }


    private val Bell.description: String
        get() =
            if (isRinging) getString(R.string.bellDescription, "ringing")
            else getString(R.string.bellDescription, "not ringing")


}