package com.example.network_termproject.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.network_termproject.databinding.ChatLeftLayoutBinding;
import com.example.network_termproject.databinding.ChatRightLayoutBinding;
import com.example.network_termproject.network.NetData;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<NetData> datas;
    private String owner;

    public ChatAdapter(ArrayList<NetData> datas, String owner) {
        this.datas = datas;
        this.owner = owner;
    }

    class LeftChatHolder extends RecyclerView.ViewHolder {

        private ChatLeftLayoutBinding binding;

        public LeftChatHolder(ChatLeftLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NetData data) {
            binding.nameTextView.setText(data.getName());
            binding.contentTextView.setText(data.getContent());
        }
    }

    class RightChatHolder extends RecyclerView.ViewHolder {

        private ChatRightLayoutBinding binding;

        public RightChatHolder(ChatRightLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NetData data) {
            binding.nameTextView.setText(data.getName());
            binding.contentTextView.setText(data.getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).getName().equals(owner)) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1) {
            return new LeftChatHolder(ChatLeftLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new RightChatHolder(ChatRightLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 1) {
            LeftChatHolder leftHolder = (LeftChatHolder) holder;
            leftHolder.bind(datas.get(position));
        } else if (holder.getItemViewType() == 2) {
            RightChatHolder rightHOlder = (RightChatHolder) holder;
            rightHOlder.bind(datas.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

}
