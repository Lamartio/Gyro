package io.lamart.gyro

import android.os.Handler
import android.os.Looper
import io.lamart.gyro.segment.Segment
import io.lamart.gyro.variables.toProperty

class Actions(private val segment: Segment<State>) {

    private var isBellRinging: Boolean by segment
        .select({ house }, { copy(house = it) })
        .select({ door }, { copy(door = it) })
        .select({ bell }, { copy(bell = it) })
        .select({ isRinging }, { copy(isRinging = it) })
        .toProperty()

    fun openDoor() =
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { !isOpen }
            .update { copy(isOpen = true) }

    fun closeDoor() =
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .filter { isOpen }
            .update { copy(isOpen = false) }

    fun startRinging() =
        segment
            .select({ house }, { copy(house = it) })
            .select({ door }, { copy(door = it) })
            .select({ bell }, { copy(bell = it) })
            .filter { !isRinging }
            .select({ isRinging }, { copy(isRinging = it) })
            .set(true)

    fun stopRinging() {
        isBellRinging = false
    }

    fun signIn(name: String, pass: String) {
        segment
            .select({ user }, { copy(user = it) })
            .filter<User.NotSignedIn>()
            .record { User.SigningIn }
            ?.takeIf { (before, after) -> before is User.NotSignedIn && after is User.SignedIn }
            ?.let { doNetworkLogin(name, pass, ::onSuccess, ::onFailure) }
    }

    private fun onSuccess(token: String) =
        segment
            .select({ user }, { copy(user = it) })
            .filter<User.SigningIn>()
            .update { User.SignedIn(token) }

    private fun onFailure(error: Throwable) =
        segment
            .select({ user }, { copy(user = it) })
            .filter<User.SigningIn>()
            .update { User.NotSignedIn(error.message) }

    private fun doNetworkLogin(
        name: String,
        pass: String,
        onSuccess: (token: String) -> Unit,
        onFailure: (error: Throwable) -> Unit
    ) {
        Looper.getMainLooper().let(::Handler).postDelayed(
            {
                if (name == "hello" && pass == "world")
                    onSuccess("MyFakeToken")
                else
                    onFailure(Throwable("401 - Unauthorized"))
            },
            2000
        )
    }

    fun signOut() =
        segment
            .select({ user }, { copy(user = it) })
            .filter<User.SignedIn>()
            .update { User.NotSignedIn() }

}