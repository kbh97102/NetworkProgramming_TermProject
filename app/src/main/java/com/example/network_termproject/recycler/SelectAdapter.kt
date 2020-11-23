package com.example.network_termproject.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.network_termproject.databinding.SelectFriendItemLayoutBinding
import com.example.network_termproject.recycler.SelectAdapter.SelectViewHolder
import java.util.*

class SelectAdapter(private val users: ArrayList<String>) : RecyclerView.Adapter<SelectViewHolder>() {

    private var views: ArrayList<SelectViewHolder>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectViewHolder {
        val viewHolder = SelectViewHolder(SelectFriendItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        views!!.add(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: SelectViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    val checkedUsers: ArrayList<String>
        get() {
            val checkedUsers = ArrayList<String>()
            for (viewHolder in views!!) {
                if (viewHolder.binding.selectCheckBox.isChecked) {
                    checkedUsers.add(viewHolder.binding.selectNameTextView.text.toString())
                }
            }
            return checkedUsers
        }

    class SelectViewHolder(val binding: SelectFriendItemLayoutBinding) : ViewHolder(binding.root) {
        fun bind(name: String?) {
            binding.selectNameTextView.text = name
            binding.selectCheckBox.isChecked = false
        }
    }

    init {
        views = ArrayList()
    }
}