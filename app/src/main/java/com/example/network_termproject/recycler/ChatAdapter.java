package com.example.network_termproject.recycler;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.network_termproject.databinding.ChatLeftLayoutBinding;
import com.example.network_termproject.databinding.ChatRightLayoutBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<JSONObject> datas;
    private String owner;

    public ChatAdapter(ArrayList<JSONObject> datas, String owner) {
        this.datas = datas;
        this.owner = owner;
    }

    class LeftChatHolder extends RecyclerView.ViewHolder{

        private ChatLeftLayoutBinding binding;

        public LeftChatHolder(ChatLeftLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(JSONObject data) throws JSONException {
            binding.nameTextView.setText(data.getString("name"));
            binding.contentTextView.setText(data.getString("content"));
        }
    }

    class RightChatHolder extends RecyclerView.ViewHolder{

        private ChatRightLayoutBinding binding;

        public RightChatHolder(ChatRightLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(JSONObject data) throws JSONException {
            binding.nameTextView.setText(data.getString("name"));
            binding.contentTextView.setText(data.getString("content"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (datas.get(position).get("name").equals(owner)){
                return 1;
            }else
            {
                return 2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == 1){
            return new LeftChatHolder(ChatLeftLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
        else{
            return new RightChatHolder(ChatRightLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try{
            if (holder.getItemViewType() == 1){
                LeftChatHolder leftHolder = (LeftChatHolder)holder;
                leftHolder.bind(datas.get(position));
            }
            else if(holder.getItemViewType() == 2){
                RightChatHolder rightHOlder = (RightChatHolder)holder;
                rightHOlder.bind(datas.get(position));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

}
