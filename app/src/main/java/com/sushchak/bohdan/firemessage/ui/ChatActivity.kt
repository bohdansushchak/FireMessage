package com.sushchak.bohdan.firemessage.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import com.google.firebase.firestore.ListenerRegistration
import com.sushchak.bohdan.firemessage.R
import com.sushchak.bohdan.firemessage.utils.FirestoreUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.toast

class ChatActivity : AppCompatActivity() {

    private lateinit var messageListenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        FirestoreUtil.getOrCreateChatChannel(otherUserId) {channelId ->
            messageListenerRegistration = FirestoreUtil.addChatMessageLIstener(channelId, this, this::onMessagesChanged)
        }
    }

    private fun onMessagesChanged(message: List<Item>) {
        toast("onMessagesChangedRunning")
    }
}
