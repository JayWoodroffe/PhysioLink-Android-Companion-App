package hu.bme.aut.fna1a3.physiolink

import adapter.MessageAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.ChatDataAccess
import data.DoctorDataAccess
import data.PatientDataHolder
import hu.bme.aut.fna1a3.physiolink.databinding.ActivityChatBinding
import model.MessageModel

class Chat : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var doctorId:String
    private lateinit var patientId:String
    private lateinit var chatId:String
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<MessageModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doctorId = PatientDataHolder.getLoggedInPatient()?.doctorID ?: ""
        patientId = PatientDataHolder.getLoggedInPatient()?.id ?: ""
        chatId = ChatDataAccess.getChatId(doctorId, patientId)

        //set up the recycler view adapter
        messageAdapter = MessageAdapter(messages)
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }



        //set up the doctors name
        getDoctorName(doctorId) { doctorName ->
            binding.tvDoctorName.text = doctorName ?: "Your physio"
        }

        //listen for incoming messages
        ChatDataAccess.listenForMessages(chatId){newMessages ->
            messages.clear()
            messages.addAll(newMessages)
            messageAdapter.notifyDataSetChanged()
            binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
        }

//        messageAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                super.onItemRangeInserted(positionStart, itemCount)
//                binding.recyclerViewMessages.scrollToPosition(messageAdapter.itemCount - 1)
//            }
//        })

        binding.buttonSend.setOnClickListener {
            val text = binding.editTextMessage.text.toString()

            if (text.isNotEmpty()) {
                ChatDataAccess.sendMessage(chatId, patientId, text)
                binding.editTextMessage.text.clear()
            }
        }

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }
    }

    private fun getDoctorName(doctorId: String, callback: (String?) -> Unit) {
        DoctorDataAccess.getDoctorNameByCertId(doctorId,
            onSuccess = { name ->
                callback(name)
            },
            onFailure = { exception ->
                println("Error fetching doctor's name: ${exception.message}")
                callback(null)
            }
        )
    }
}