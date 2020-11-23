package com.example.network_termproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.network_termproject.databinding.ChatRoomLayoutBinding;
import com.example.network_termproject.network.Client;
import com.example.network_termproject.network.NetData;
import com.example.network_termproject.recycler.ChatAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ChatRoom extends AppCompatActivity {

    /*
    채팅방이 가져야할 정보들
    1. 사용자 그룹 (1:1) or 그룹 -> arraylist쓰자
    3. 카톡 내용들 -> 내부 저장소에 캐시 형태로 저장하면 될 것 같다.
     */
    private String talkDataPath;
    private String roomName;
    private ChatRoomLayoutBinding binding;
    private ChatAdapter chatAdapter;
    private ArrayList<NetData> datas;
    private NetData.Builder dataBuilder;
    private ChatRoomInfo chatRoomInfo;
    private InputMethodManager imm;
    int rootHeight = -1;
    private boolean isEmojiSelected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ChatRoomLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        binding.chatRoomRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (rootHeight == -1) {
                    rootHeight = binding.chatRoomRootView.getHeight();
                }
                Rect visibleFrameSize = new Rect();
                binding.chatRoomRootView.getWindowVisibleDisplayFrame(visibleFrameSize);
                int heightExceptKeyboard = visibleFrameSize.bottom - visibleFrameSize.top;
                if (heightExceptKeyboard < rootHeight) {
                    int keyboardHeight = rootHeight - heightExceptKeyboard;
                    binding.emojiContainer.setMaxHeight(keyboardHeight);
                }
            }
        });

        Bundle bundle = getIntent().getBundleExtra("chatRoomInfo");
        assert bundle != null;
        chatRoomInfo = (ChatRoomInfo) bundle.getSerializable("chatRoomInfo");

        init();

        binding.chatRoomToolbar.setTitle(chatRoomInfo.getRoom_id());

        binding.icon1.setOnClickListener(view -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) binding.icon1.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, baos);
            String base64Data = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            dataBuilder.setType("image").setContent(base64Data);
            isEmojiSelected = true;
        });

        chatAdapter = new ChatAdapter(datas, Client.getInstance().getName());
        binding.chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRoomRecyclerView.setAdapter(chatAdapter);
        binding.chatRoomSendButton.setOnClickListener((v) -> {

            //TODO 테스트용임 이모티콘 (이미지)보내기
            if (isEmojiSelected) {
                NetData clientData = dataBuilder
                        .setName(Client.getInstance().getName())
                        .setUserId(Client.getInstance().getId())
                        .setRoomId(chatRoomInfo.getRoom_id())
                        .build();
                datas.add(clientData);
                Client.getInstance().write(clientData);
                chatAdapter.notifyDataSetChanged();
                isEmojiSelected = false;
                binding.emojiContainer.setVisibility(View.INVISIBLE);
                binding.emojiContainer.setFocusable(false);
            } else {
                NetData clientData = dataBuilder.setType("text")
                        .setName(Client.getInstance().getName())
                        .setUserId(Client.getInstance().getId())
                        .setRoomId(chatRoomInfo.getRoom_id())
                        .setContent(binding.chatRoomEditText.getText().toString())
                        .build();
                datas.add(clientData);
                Client.getInstance().write(clientData);
                chatAdapter.notifyDataSetChanged();
                binding.chatRoomEditText.setText("");
            }

        });


        binding.chatRoomEmojiButton.setOnClickListener(view -> {
            if (imm.isAcceptingText()) {
                hideKeyboard();
            }
            if (binding.emojiContainer.getVisibility() == View.INVISIBLE) {
                binding.emojiContainer.setVisibility(View.VISIBLE);
                binding.emojiContainer.setFocusable(true);
            } else {
                binding.emojiContainer.setVisibility(View.INVISIBLE);
                binding.emojiContainer.setFocusable(false);
            }
        });

        chatAdapter.notifyDataSetChanged();

    }

    private void hideKeyboard() {
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void init() {
        datas = new ArrayList<>();
        Client.getInstance().setDisplay(this::display);
        Client.getInstance().setName("testName");
        dataBuilder = new NetData.Builder();
    }


    private void display(NetData data) {
        runOnUiThread(() -> {
            datas.add(data);
            chatAdapter.notifyDataSetChanged();
        });
    }

    public void getTalkData() {

    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Client.getInstance().disconnect();
    }
}
