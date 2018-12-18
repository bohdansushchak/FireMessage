package com.sushchak.bohdan.firemessage.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.sushchak.bohdan.firemessage.R
import com.sushchak.bohdan.firemessage.model.ImageMessage
import com.sushchak.bohdan.firemessage.model.TextMessage
import com.sushchak.bohdan.firemessage.model.User
import com.sushchak.bohdan.firemessage.utils.FirestoreUtil
import com.sushchak.bohdan.firemessage.utils.StorageUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*
import android.view.View
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class ChatActivity : AppCompatActivity() {

    private val RC_SELECT_IMAGE = 2

    private lateinit var currentChannelId: String
    private lateinit var currentUser: User
    private lateinit var otherUserId: String

    private lateinit var messageListenerRegistration: ListenerRegistration
    private var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        FirestoreUtil.getCurrentUser(::onSuccessGetUser, ::onFailedGestUser)

        otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        FirestoreUtil.getOrCreateChatChannel(otherUserId) { channelId ->
            currentChannelId = channelId

            messageListenerRegistration =
                    FirestoreUtil.addChatMessagesListener(channelId, this, this::updateRecyclerView)

            imageView_send.setOnClickListener {
                val messageToSend =
                    TextMessage(
                        editText_message.text.toString(), Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        otherUserId,
                        currentUser.name
                    )

                editText_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            fab_send_image.setOnClickListener {

                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }
                startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_select_image)), RC_SELECT_IMAGE)
            }

            editText_message.viewTreeObserver.addOnGlobalLayoutListener {

                if (keyboardShown(editText_message.rootView)) {
                    scrollToLastMessage()
                }
            }
        }
    }

    private fun keyboardShown(rootView: View): Boolean {

        val softKeyboardHeight = 100
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val dm = rootView.getResources().getDisplayMetrics()
        val heightDiff = rootView.getBottom() - r.bottom
        return heightDiff > softKeyboardHeight * dm.density
    }

    private fun updateRecyclerView(messages: List<Item>) {

        fun init() {
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)
        if (shouldInitRecyclerView)
            init()
        else
            updateItems()

        scrollToLastMessage()
    }

    private fun scrollToLastMessage() =
        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter!!.itemCount - 1)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data

            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val selecteImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selecteImageBytes) { imagePath ->
                val messageToSend = ImageMessage(
                    imagePath,
                    Calendar.getInstance().time,
                    FirebaseAuth.getInstance().currentUser!!.uid, otherUserId,
                    currentUser.name
                )

                FirestoreUtil.sendMessage(messageToSend, currentChannelId)
            }
        }
    }

    private fun onSuccessGetUser(user: User) {
        currentUser = user
    }

    private fun onFailedGestUser(e: Exception) {
        FirebaseAuth.getInstance().signOut()
        startActivity(intentFor<SignInActivity>().newTask().clearTask())
    }
}
