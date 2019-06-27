package io.lamart.gyro.actions

import android.os.Handler
import android.os.Looper
import io.lamart.gyro.User
import io.lamart.gyro.segments.Segment
import io.lamart.gyro.variables.toProperty

class UserActions(private val segment: Segment<User>) {

    private var user by segment.toProperty()

    fun signIn(name: String, pass: String) {
        segment
            .filter<User.NotSignedIn>()
            .update { User.SigningIn }
            ?.let { someNetworkSignIn(name, pass, ::onSuccess, ::onFailure) }
    }

    private fun onSuccess(token: String) {
        user = User.SignedIn(token)
    }

    private fun onFailure(error: Throwable) {
        user = User.NotSignedIn(error.message)
    }

    fun signOut() {
        user = User.NotSignedIn()
    }

}

private fun someNetworkSignIn(
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
