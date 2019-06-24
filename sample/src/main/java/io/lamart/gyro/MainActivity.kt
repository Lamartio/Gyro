package io.lamart.gyro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.lamart.gyro.fragments.BellFragment
import io.lamart.gyro.fragments.DoorFragment
import io.lamart.gyro.fragments.UserFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.bellContent, DoorFragment())
                .add(R.id.doorContent, BellFragment())
                .add(R.id.userContent, UserFragment())
                .commit()
        }
    }

}
