package com.sushchak.bohdan.firemessage.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.sushchak.bohdan.firemessage.utils.FirestoreUtil
import java.lang.NullPointerException

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val newRegistrationToken = FirebaseInstanceId.getInstance().token

        if(FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(newRegistrationToken)
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if(newRegistrationToken == null) throw NullPointerException("FCM token is null")
            FirestoreUtil.getFCMRegistrationTokens { tokens ->
                if(tokens.contains(newRegistrationToken))
                    return@getFCMRegistrationTokens

                tokens.add(newRegistrationToken)
                FirestoreUtil.setFCMRegistrationTokens(tokens)
            }
        }
    }
}