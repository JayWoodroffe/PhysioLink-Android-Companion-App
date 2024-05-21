package adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.fna1a3.physiolink.R
import hu.bme.aut.fna1a3.physiolink.databinding.MessageCardBinding
import model.MessageModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MessageAdapter(private val messages: List<MessageModel>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)

        holder.setAlignment(message.senderId.matches(Regex("\\d+")))
        // Check the senderId of the message
       // val screenWidth = getScreenWidth(holder.itemView.context)
        //val estimatedWidth = holder.setAlignment(isLeftAligned = !message.senderId.matches(Regex("\\d+")), screenWidth = screenWidth)

//        // Set the alignment of the message TextView based on the condition
//        val params = holder.itemView.layoutParams as RelativeLayout.LayoutParams
//        if (isLeftAligned) {
//            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//        } else {
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//        }
//        holder.itemView.layoutParams = params
    }

    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val screenWidth = display.width

        Log.d("Chat", "Screen width: $screenWidth")
        return screenWidth
    }

    override fun getItemCount(): Int = messages.size


    class MessageViewHolder(private val binding: MessageCardBinding) : RecyclerView.ViewHolder(binding.root) {
        private var messageTextViewWidth = 0
        fun bind(message: MessageModel) {
            binding.textViewMessage.text = message.text

            // Wait until the layout phase to get the actual width of the message TextView
//            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    // Retrieve the width of the message TextView
//                    messageTextViewWidth = binding.textViewMessage.width
//
//                    // Remove the listener to avoid redundant calls
//                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            })
            // Convert timestamp to human-readable hour and minute
            val timestamp = message.timestamp
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = dateFormat.format(calendar.time)

            // Display formatted hour and minute
            binding.textViewTimestamp.text = formattedTime

            // Set background based on senderId
            if (message.senderId.matches(Regex("\\d+"))) {
                // Set background for right-side message
                binding.root.setBackgroundResource(R.drawable.chat_message_bg_right)
            } else {
                // Set background for left-side message
                binding.root.setBackgroundResource(R.drawable.chat_message_bg_left)
            }

            // Optionally handle image messages
            // Load image using your preferred image loading library (e.g., Glide, Picasso)
        }
        fun setAlignment(isLeftAligned: Boolean) {
//            val density = itemView.context.resources.displayMetrics.density
//            val messageLength = binding.textViewMessage.text.length
//            val estimatedWidth = (messageLength * textSizeInSp * density).toInt()

            val params = itemView.layoutParams as RecyclerView.LayoutParams
            params.width = RecyclerView.LayoutParams.WRAP_CONTENT // Set the width to wrap content
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT // Set the height to wrap content

            if (isLeftAligned) {
                params.marginStart = 0 // Set left margin
                params.marginEnd = 150 // Reset right margin
            } else {
                params.marginStart = 550 // Set left margin
                params.marginEnd = 0// Reset right margin
            }

            itemView.layoutParams = params
        }

    }
}