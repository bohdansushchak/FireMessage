package com.sushchak.bohdan.firemessage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.sushchak.bohdan.firemessage.ui.SignInActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity<SignInActivity>()
        else
            startActivity<MainActivity>()
        finish()
    }
}
