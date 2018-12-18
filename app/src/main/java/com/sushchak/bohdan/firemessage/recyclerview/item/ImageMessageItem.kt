package com.sushchak.bohdan.firemessage.recyclerview.item

import android.content.Context
import com.sushchak.bohdan.firemessage.R
import com.sushchak.bohdan.firemessage.glide.GlideApp
import com.sushchak.bohdan.firemessage.model.ImageMessage
import com.sushchak.bohdan.firemessage.utils.StorageUtil
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_image_message.*

class ImageMessageItem(val message: ImageMessage,
                       val context: Context) : MessageItem(message) {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        GlideApp.with(context)
            .load(StorageUtil.pathToReference(message.imagePath))
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_broken_image)
            .into(viewHolder.imageView_message_image)
    }

    override fun getLayout(): Int = R.layout.item_image_message

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is ImageMessageItem)
            return false
        if(this.message != other.message)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as? TextMessageItem)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }


}