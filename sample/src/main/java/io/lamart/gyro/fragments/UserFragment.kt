package io.lamart.gyro.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import io.lamart.gyro.R
import io.lamart.gyro.User
import io.lamart.gyro.utils.actions

class UserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.user, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signInView = view.findViewById<ViewGroup>(R.id.signIn)
        val homeView = view.findViewById<ViewGroup>(R.id.home)
        val nameView = view.findViewById<EditText>(R.id.name)
        val passView = view.findViewById<EditText>(R.id.pass)
        val signInButton = view.findViewById<Button>(R.id.doSignIn)
        val signOutButton = view.findViewById<Button>(R.id.doSignOut)
        val loadingView = view.findViewById<ProgressBar>(R.id.loading)
        val errorView = view.findViewById<TextView>(R.id.error)

        signInButton.setOnClickListener {
            actions.user.signIn(
                nameView.text.toString().trim(),
                passView.text.toString().trim()
            )
        }
        signOutButton.setOnClickListener { actions.user.signOut() }

        actions
            .data
            .map { it.user !is User.SignedIn }
            .distinctUntilChanged()
            .observe(this, Observer { signInView.isVisible = it })
        actions
            .data
            .map { it.user is User.SignedIn }
            .distinctUntilChanged()
            .observe(this, Observer { homeView.isVisible = it })
        actions
            .data
            .map { it.user is User.SigningIn }
            .distinctUntilChanged()
            .observe(this, Observer { loadingView.isVisible = it })
        actions
            .data
            .map {
                when (it.user) {
                    is User.NotSignedIn -> it.user.reason
                    else -> null
                }
            }
            .distinctUntilChanged()
            .observe(this, Observer {
                errorView.text = it
                errorView.isVisible = it != null
            })
    }

}
