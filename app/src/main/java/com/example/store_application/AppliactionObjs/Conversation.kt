package com.example.store_application.AppliactionObjs

import com.example.store_application.AppliactionObjs.User
import java.io.Serializable

data class Conversation ( var conversationId: String, var reciever: User?,var newMessagesList : Array<Message>? ) : Serializable{
    constructor() : this("",null , null)
}
