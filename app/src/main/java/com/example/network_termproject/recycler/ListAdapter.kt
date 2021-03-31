package com.example.network_termproject.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.network_termproject.ChatRoomInfo
import com.example.network_termproject.databinding.ChatListItemLayoutBinding
import com.example.network_termproject.recycler.ListAdapter.ListHolder
import java.util.*
import java.util.function.Consumer

class ListAdapter(private val chatRoomInfos: ArrayList<ChatRoomInfo>?, private val context: Context) : RecyclerView.Adapter<ListHolder>() {

    private var intentChatRoom: Consumer<ChatRoomInfo>? = null

    fun setIntentChatRoom(intentChatRoom: Consumer<ChatRoomInfo>?) {
        this.intentChatRoom = intentChatRoom
    }

    inner class ListHolder(val binding: ChatListItemLayoutBinding) : ViewHolder(binding.root) {
        fun bind(chatRoom: ChatRoomInfo) {
            val nameText = "${chatRoom.room_id}'s Room"
            binding.chatListNameTextView.text = nameText
            if (Objects.nonNull(chatRoom.user_ids)){
                binding.chatListUsersNumberTextView.text = (chatRoom.user_ids!!.size).toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        val binding = ChatListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        holder.binding.root.setOnClickListener { intentChatRoom!!.accept(chatRoomInfos!![position]) }
        val selectedChatRoom = chatRoomInfos!![position]
        holder.bind(selectedChatRoom)
    }

    override fun getItemCount(): Int {
        return chatRoomInfos!!.size
    }
}