package io.lamart.gyro

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map

val AppCompatActivity.mainApplication
    get() = application as Main

class MainActivity : AppCompatActivity(), Main {

    override val data: LiveData<State> by lazy { mainApplication.data }
    override val actions: Actions by lazy { mainApplication.actions }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDoor()
        initBell()
    }

    private fun initDoor() {
        val doorDescription = findViewById<TextView>(R.id.doorDescription)
        val openDoor = findViewById<Button>(R.id.openDoor)
        val closeDoor = findViewById<Button>(R.id.closeDoor)

        openDoor.setOnClickListener { actions.openDoor() }
        closeDoor.setOnClickListener { actions.closeDoor() }

        data
            .map { state -> state.house.door.description }
            .distinctUntilChanged()
            .observe(this, doorDescription::setText)
        data
            .map { state -> state.house.door.isOpen }
            .distinctUntilChanged()
            .observe(this, closeDoor::setEnabled)
        data
            .map { state -> !state.house.door.isOpen }
            .distinctUntilChanged()
            .observe(this, openDoor::setEnabled)
    }

    private fun initBell() {
        val bellDescription = findViewById<TextView>(R.id.bellDescription)
        val startRinging = findViewById<Button>(R.id.startRinging)
        val stopRinging = findViewById<Button>(R.id.stopRinging)

        startRinging.setOnClickListener { actions.startRinging() }
        stopRinging.setOnClickListener { actions.stopRinging() }

        data
            .map { state -> state.house.door.bell.description }
            .distinctUntilChanged()
            .observe(this, bellDescription::setText)
        data
            .map { state -> !state.house.door.isOpen && !state.house.door.bell.isRinging }
            .distinctUntilChanged()
            .observe(this, startRinging::setEnabled)
        data
            .map { state -> state.house.door.bell.isRinging }
            .distinctUntilChanged()
            .observe(this, stopRinging::setEnabled)
    }

    private val Door.description: String
        get() =
            if (isOpen) getString(R.string.doorDescription, "open")
            else getString(R.string.doorDescription, "closed")

    private val Bell.description: String
        get() =
            if (isRinging) getString(R.string.bellDescription, "ringing")
            else getString(R.string.bellDescription, "not ringing")

}
