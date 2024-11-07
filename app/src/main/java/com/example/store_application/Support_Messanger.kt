package com.example.store_application

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.store_application.Adapters.MessageAdapter
import com.example.store_application.AppliactionObjs.Message
import com.example.store_application.AppliactionObjs.User
import com.example.store_application.databinding.ActivitySupportMessangerBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Support_Messanger : AppCompatActivity() {
    lateinit var binding : ActivitySupportMessangerBinding
    lateinit var sender:User
    lateinit var receiver :User
    var userExists = false
    val firebaseDB = FirebaseDatabase.getInstance()
    val messageAdapter = MessageAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportMessangerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getConversationEdges()
        getSupportConversation()
        removingNewMessages()
        goBack()

        if (receiver != null && sender != null) {
         setMessageList()
            getMessages()
           binding.sendMessageBtn.setOnClickListener {
               val messageText = binding.messageInput.text.toString()
if(!messageText.isEmpty()){
    binding.messageInput.setText("")
    initRooms(messageText)
if (!userExists){
    val conRef = firebaseDB.getReference("SupportConversations")
    conRef.child(sender.userId).setValue(sender)
    userExists = true
}
}
 }
        }
    }
    private fun goBack() {
        binding.messangerGoBack.setOnClickListener {
            finish()
        }
    }

    private fun getSupportConversation() {
        val conRef = firebaseDB.getReference("SupportConversations")
        conRef.orderByKey().equalTo(sender.userId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    userExists = true
                }
            }
            override fun onCancelled(p0: DatabaseError) {
             Toast.makeText(baseContext,"Operation Canceled",Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getMessages() {
        val roomRef = firebaseDB.getReference("Rooms")
        val senderRoom = roomRef.child(sender.userId + receiver.userId)
val messageList :ArrayList<Message> = ArrayList()
        senderRoom.child("Messages").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(items in p0.children){
                        var message = items.getValue(Message::class.java)
                        if (message != null ){
                            messageList.add(message)
                        }

                    }
                    if(!messageList.isEmpty()){
messageAdapter.setListMessage(messageList)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setMessageList() {
      messageAdapter.setUserSender(sender)
        binding.messageList.layoutManager = LinearLayoutManager(this)
        binding.messageList.setHasFixedSize(true)
        binding.messageList.adapter  = messageAdapter
    }

    private fun initRooms(messageText : String) {
        val roomRef = firebaseDB.getReference("Rooms")
        val receiverRoom = roomRef.child(receiver.userId + sender.userId)
        val senderRoom = roomRef.child(sender.userId + receiver.userId )
        val messageId = roomRef.push().key.toString()
        val message = Message(messageId,messageText,sender.userId)
        receiverRoom.child("Messages").child(messageId).setValue(message).addOnCompleteListener { task->
            if(task.isSuccessful){
                receiverRoom.child("NewMessage").child(messageId).setValue(message)
                senderRoom.child("Messages").child(messageId).setValue(message).addOnCompleteListener {
              setNewMessage(message)
                }
            }
        }
        getNewMessage(message,senderRoom)
    }

    private fun setNewMessage(newMessage:Message) {
messageAdapter.addMessage(newMessage)
        binding.messageList.smoothScrollToPosition(messageAdapter.itemCount - 1)
    }

    private fun getNewMessage(message: Message , senderRoom: DatabaseReference) {
        senderRoom.child("Messages").child(message.messageId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
              var newMessage = p0.getValue(Message::class.java)
                     if(newMessage != null){
                             messageAdapter.addMessage(message)
                         binding.messageList.smoothScrollToPosition(messageAdapter.itemCount - 1)
                     }

                    }
                }


            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun getConversationEdges() {
        val edgesIntent = intent
        sender =( edgesIntent.getSerializableExtra("mainSender") as? User)!!
        receiver =( edgesIntent.getSerializableExtra("mainRceiver") as? User)!!


    }
    private fun removingNewMessages() {
        var senderNewMessageRoom = firebaseDB.getReference("Rooms").child( sender.userId + receiver.userId).child("NewMessage")
        senderNewMessageRoom.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    for(item in p0.children){
                        var newMessage = item.getValue(Message::class.java)
                        if(newMessage != null){
                            senderNewMessageRoom.child(newMessage.messageId).removeValue()
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}
