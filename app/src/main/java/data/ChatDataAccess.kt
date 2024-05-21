package data

import android.util.Log

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import model.MessageModel

object ChatDataAccess {
    private val db = FirebaseFirestore.getInstance()

    fun getChatId(doctorId: String, patientId: String):String
    {
        return "$doctorId-$patientId"
    }
    fun getMessagesRef(chatId: String): CollectionReference {
        return db.collection("chats").document(chatId).collection("messages")
    }


    //add a new chat document - happens at the start of
    fun startChat(doctorId: String, patientId: String) {
        val chatId = getChatId(doctorId, patientId)
        val chatDocRef = db.collection("chats").document(chatId)
        val chatData = mapOf(
            "doctorId" to doctorId,
            "patientId" to patientId,
            "lastMessage" to "",
            "lastUpdated" to System.currentTimeMillis()
        )
        chatDocRef.set(chatData, SetOptions.merge())
    }

    //sending a new message
    fun sendMessage(chatId: String, senderId: String, text: String, imageUrl: String? = null) {
        val messagesRef = getMessagesRef(chatId)
        val message = hashMapOf(
            "senderId" to senderId, //sender id will be the patient's id
            "text" to text,
            "timestamp" to System.currentTimeMillis(),
            "imageUrl" to imageUrl
        )

        // Add message to messages sub-collection
        messagesRef.add(message)

        // Update chat document with last message and timestamp
        val chatDocRef = db.collection("chats").document(chatId)
        chatDocRef.update(mapOf(
            "lastMessage" to text,
            "lastUpdated" to System.currentTimeMillis()
        ))
    }

    //to listen for messages:
    fun listenForMessages(chatId: String, onMessagesUpdate: (List<MessageModel>) -> Unit) {
        val messagesRef = db.collection("chats").document(chatId).collection("messages")
        messagesRef.orderBy("timestamp").addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Handle error
                Log.e("ChatDataAccess", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                for (doc in snapshot.documents) {
                    Log.d("ChatDataAccess", doc.data.toString())
                }
                val messages = snapshot.toObjects(MessageModel::class.java)
                onMessagesUpdate(messages)
            } else {
                Log.d("ChatDataAccess", "Current data: null")
            }
        }
    }
}