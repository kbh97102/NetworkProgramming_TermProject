package com.example.network_termproject.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.network_termproject.ImageDialog
import com.example.network_termproject.databinding.ChatLeftLayoutBinding
import com.example.network_termproject.databinding.ChatRightLayoutBinding
import com.example.network_termproject.network.NetData
import java.util.*

class ChatAdapter(private val datas: ArrayList<NetData>, private val owner: String, private val supportFragmentManager: FragmentManager) : RecyclerView.Adapter<ViewHolder>() {
    internal inner class LeftChatHolder(val binding: ChatLeftLayoutBinding) : ViewHolder(binding.root) {
        fun bind(data: NetData) {
            binding.nameTextView.text = data.getName()
            if (data.getType() == "text") {
                binding.contentImageView.visibility = View.INVISIBLE
                binding.contentImageView.isFocusable = false
                binding.contentTextView.text = data.getContent()
            } else {
                binding.contentTextView.visibility = View.INVISIBLE
                binding.contentTextView.isFocusable = false
                binding.contentImageView.apply {
                    setImageBitmap(data.getImage())
                }
                Log.e("LeftChatHolder", "${binding.contentImageView.width} + ${binding.contentImageView.height} ")
            }
        }
    }

    internal inner class RightChatHolder(val binding: ChatRightLayoutBinding) : ViewHolder(binding.root) {
        fun bind(data: NetData) {
            binding.nameTextView.text = data.getName()
            if (data.getType() == "text") {
                binding.contentImageView.visibility = View.INVISIBLE
                binding.contentImageView.isFocusable = false
                binding.contentTextView.text = data.getContent()
            } else {
                binding.contentTextView.visibility = View.INVISIBLE
                binding.contentTextView.isFocusable = false
                binding.contentImageView.setImageBitmap(data.getImage())
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (datas[position].getName() == owner) {
            1
        } else {
            2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            LeftChatHolder(ChatLeftLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            RightChatHolder(ChatRightLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.itemViewType == 1) {
            val leftHolder = holder as LeftChatHolder
            leftHolder.bind(datas[position])
            holder.binding.root.setOnClickListener {
                val testDialog = ImageDialog(datas[position].getImage())
                testDialog.show(supportFragmentManager, "Tag")
            }
        } else if (holder.itemViewType == 2) {
            val rightHOlder = holder as RightChatHolder
            rightHOlder.bind(datas[position])
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

}