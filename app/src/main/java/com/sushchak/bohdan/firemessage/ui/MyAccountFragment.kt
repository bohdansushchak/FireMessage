package com.sushchak.bohdan.firemessage.ui


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

import com.sushchak.bohdan.firemessage.R
import com.sushchak.bohdan.firemessage.glide.GlideApp
import com.sushchak.bohdan.firemessage.model.User
import com.sushchak.bohdan.firemessage.utils.FirestoreUtil
import com.sushchak.bohdan.firemessage.utils.StorageUtil
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import java.io.ByteArrayOutputStream
import java.lang.Exception

class MyAccountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        view.apply {
            view.imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
                }

                startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_select_image)), RC_SELECT_IMAGE)
            }
            btn_save.setOnClickListener {
                if (::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(
                            editText_name.text.toString(),
                            editText_bio.text.toString(),
                            imagePath
                        )
                    }
                else
                    FirestoreUtil.updateCurrentUser(
                        editText_name.text.toString(),
                        editText_bio.text.toString(),
                        null
                    )
            }

            btn_sign_out.setOnClickListener {
                AuthUI.getInstance()
                    .signOut(this@MyAccountFragment.context!!)
                    .addOnCompleteListener {
                        startActivity(intentFor<SignInActivity>().newTask().clearTask())
                    }
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK
            && data != null && data.data != null
        ) {
            val selectedImagePath = data.data
            val selectedIageBmp = MediaStore.Images.Media
                .getBitmap(activity?.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedIageBmp.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            GlideApp.with(this)
                .load(selectedImageBytes)
                .placeholder(R.drawable.image_account)
                .into(imageView_profile_picture)


            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser(::successGetUser, ::failsedLoadUser)
    }

    private fun successGetUser(user: User) {
        if (this@MyAccountFragment.isVisible) {

            editText_name.setText(user.name)
            editText_bio.setText(user.bio)

            if (!pictureJustChanged && user.profilePicturePath != null)
                GlideApp.with(this)
                    .load(StorageUtil.pathToReference(user.profilePicturePath))
                    .placeholder(R.drawable.image_account)
                    .into(imageView_profile_picture)
        }
    }

    private fun failsedLoadUser(e: Exception) {
        FirebaseAuth.getInstance().signOut()
        startActivity(intentFor<SignInActivity>().newTask().clearTask())
    }
}
