package com.sushchak.bohdan.firemessage.recyclerview.item

import android.content.Context
import com.sushchak.bohdan.firemessage.R
import com.sushchak.bohdan.firemessage.model.Message
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder

class TextMessageItem(val message: Message,
                      val context: Context) : Item() {


    override fun bind(viewHolder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayout(): Int = R.layout.item_text_message
}