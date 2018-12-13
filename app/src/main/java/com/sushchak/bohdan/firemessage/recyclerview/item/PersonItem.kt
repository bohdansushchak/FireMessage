package com.sushchak.bohdan.firemessage.recyclerview.item

import android.content.Context
import com.sushchak.bohdan.firemessage.R
import com.sushchak.bohdan.firemessage.glide.GlideApp
import com.sushchak.bohdan.firemessage.model.User
import com.sushchak.bohdan.firemessage.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*

class PersonItem(val person: User,
                 val userId: String,
                 private val context: Context): Item() {
    override fun getLayout(): Int = R.layout.item_person

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if(person.profilePicturePath != null){
            GlideApp.with(context)
                .load(StorageUtil.pathToReference(person.profilePicturePath))
                .into(viewHolder.imageView_profile_picture)
        }
    }
}