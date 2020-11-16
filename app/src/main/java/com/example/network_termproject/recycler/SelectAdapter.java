package com.example.network_termproject.recycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.network_termproject.databinding.SelectFriendItemLayoutBinding;

import java.util.ArrayList;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.SelectViewHolder> {

    private ArrayList<String> users;
    private ArrayList<SelectViewHolder> views;

    public SelectAdapter(ArrayList<String> users) {
        this.users = users;
        views = new ArrayList<>();
    }

    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SelectViewHolder viewHolder = new SelectViewHolder(SelectFriendItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        views.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public ArrayList<String> getCheckedUsers() {
        ArrayList<String> checkedUsers = new ArrayList<>();
        for (SelectViewHolder viewHolder : views) {
            if (viewHolder.binding.selectCheckBox.isChecked()) {
                checkedUsers.add(viewHolder.binding.selectNameTextView.getText().toString());
            }
        }
        return checkedUsers;
    }

    class SelectViewHolder extends RecyclerView.ViewHolder {

        private SelectFriendItemLayoutBinding binding;

        public SelectViewHolder(SelectFriendItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String name) {
            binding.selectNameTextView.setText(name);
            binding.selectCheckBox.setChecked(false);
        }
    }
}
