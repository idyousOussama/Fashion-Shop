package com.example.store_application.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.store_application.AppliactionObjs.Message
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.R

class MessageAdapter () : RecyclerView.Adapter<MessageAdapter.MessageCustomViewHolder>() {
  var messagesList :ArrayList<Message> = ArrayList()
     var sender: User? = null
fun setListMessage(messageList:ArrayList<Message>){
   this.messagesList = messageList
    notifyDataSetChanged()
}
    fun setUserSender(sender :User){
        this.sender = sender
        notifyDataSetChanged()
    }
    fun addMessage(newMessage : Message){
        messagesList.add(newMessage)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageCustomViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.messages_custom,p0,false)
   return MessageCustomViewHolder(view)
    }

    override fun getItemCount(): Int {
       return messagesList.size
    }

    override fun onBindViewHolder(p0: MessageCustomViewHolder, p1: Int) {
        val message = messagesList.get(p1)
        p0.setMessage(message,sender!!)
    }
    class MessageCustomViewHolder(itemView: View) : ViewHolder(itemView) {
        var sentMessage = itemView.findViewById<TextView>(R.id.sent_message_custom)
        var recievedMessage = itemView.findViewById<TextView>(R.id.recieved_message_custom)
        var failedText = itemView.findViewById<TextView>(R.id.failed_text)
fun  setMessage (message: Message , sender:User){
    if (message.senderId == sender.userId){
        sentMessage.setText(message.messageText)
        sentMessage.visibility = View.VISIBLE
        recievedMessage.visibility = View.GONE
    }else{
        recievedMessage.setText(message.messageText)
        sentMessage.visibility = View.GONE
        recievedMessage.visibility = View.VISIBLE

    }
}
    }
}