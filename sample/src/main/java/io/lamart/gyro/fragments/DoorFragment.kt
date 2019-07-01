package io.lamart.gyro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import io.lamart.gyro.Door
import io.lamart.gyro.R
import io.lamart.gyro.utils.actions
import io.lamart.gyro.utils.data

class DoorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.door, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val doorDescription = view.findViewById<TextView>(R.id.doorDescription)
        val openDoor = view.findViewById<Button>(R.id.openDoor)
        val closeDoor = view.findViewById<Button>(R.id.closeDoor)

        openDoor.setOnClickListener { actions.openDoor() }
        closeDoor.setOnClickListener { actions.closeDoor() }

        data
            .map { state -> state.house.door.description }
            .distinctUntilChanged()
            .observe(this, Observer { doorDescription.text = it })
        data
            .map { state -> state.house.door.isOpen }
            .distinctUntilChanged()
            .observe(this, Observer { closeDoor.isEnabled = it })
        data
            .map { state -> !state.house.door.isOpen }
            .distinctUntilChanged()
            .observe(this, Observer { openDoor.isEnabled = it })
    }


    private val Door.description: String
        get() =
            if (isOpen) getString(R.string.doorDescription, "open")
            else getString(R.string.doorDescription, "closed")

}