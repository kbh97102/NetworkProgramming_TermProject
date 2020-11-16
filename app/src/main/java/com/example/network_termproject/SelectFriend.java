package com.example.network_termproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.network_termproject.databinding.SelectFriendLayoutBinding;
import com.example.network_termproject.network.Client;
import com.example.network_termproject.network.NetData;
import com.example.network_termproject.recycler.SelectAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class SelectFriend extends AppCompatActivity {

    private SelectFriendLayoutBinding binding;
    private ArrayList<String> users;
    private SelectAdapter selectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SelectFriendLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        users = new ArrayList<>();
        selectAdapter = new SelectAdapter(users);
        binding.selectRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.selectRecyclerView.setAdapter(selectAdapter);
        binding.selectRecyclerView.setHasFixedSize(true);

        setSupportActionBar(binding.selectToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.selectOkButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("selectedUserList", selectAdapter.getCheckedUsers());
            setResult(RESULT_OK, intent);
            finish();
        });

        requestList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home){
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            Log.d("Options", "in");
        }
        return false;
    }

    private void requestList() {
        NetData.Builder builder = new NetData.Builder();
        Client.getInstance().setList(this::setList);
        Client.getInstance().write(builder.setType("requestList").setUserId(Client.getInstance().getId()).build());
    }

    private void setList(NetData serverData) {
        users.clear();
//        users.addAll(serverData.getList());
//        selectAdapter.notifyDataSetChanged();
        runOnUiThread(() -> {
            for(int i=0;i<serverData.getList().size();i++){
                users.add(serverData.getList().get(i));
                selectAdapter.notifyItemInserted(i);
            }
        });

    }

}
