package com.example.network_termproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network_termproject.databinding.ChatListLayoutBinding
import com.example.network_termproject.network.AnotherClient
import com.example.network_termproject.network.Client
import com.example.network_termproject.network.NetData
import com.example.network_termproject.recycler.ListAdapter
import java.nio.ByteBuffer
import java.util.*
import java.util.function.BiConsumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatListActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val listRequestCode = 10
    private var binding: ChatListLayoutBinding? = null
    private var chatRooms: HashMap<ChatRoomInfo, ChatRoom>? = null
    private var chatRoomInfos: ArrayList<ChatRoomInfo>? = null
    private var users: ArrayList<AnotherClient>? = null
    private var listAdapter: ListAdapter? = null
    private var dataBuilder: NetData.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatListLayoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, 1)
        } else {
            Log.d("Permission", "ok")
        }

        init()

        listAdapter = ListAdapter(chatRoomInfos, this)
        listAdapter!!.setIntentChatRoom { chatRoomInfo: ChatRoomInfo -> intentChatRoom(chatRoomInfo) }
        binding!!.apply {
            chatListRecyclerView.layoutManager = LinearLayoutManager(this@ChatListActivity)
            chatListRecyclerView.adapter = listAdapter
        }

        binding!!.chatListAddButton.setOnClickListener {
            val intent = Intent()
            intent.setClass(this, SelectFriend::class.java)
            startActivityForResult(intent, listRequestCode)
        }
    }

    private fun checkPermissions() =
            permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == listRequestCode && Objects.nonNull(data)) {

            val userList = data!!.getStringArrayListExtra("selectedUserList")!!
            if (Objects.isNull(userList) || userList.size <= 0) {
                return
            }
            //선택된 친구 아이디 리스트에 자기 자신도 포함
            userList.add(Client.instance.id)
            //본인 포함 선택된 친구들 리스트와 함께 채팅방 생성 요청
            val mainData = dataBuilder!!.setType("requestAdd")
                    .setUserId(Client.instance.id!!)
                    .setList(userList)
                    .build()
            val header = ByteBuffer.allocate(6)
            header.putChar('s')
            header.putInt(mainData.data.toString().toByteArray().size)
            header.flip()

            var buffer = ByteBuffer.allocate(6 + mainData.data.toString().toByteArray().size)
            buffer.apply {
                put(header)
                put(mainData.data.toString().toByteArray())
            }
            buffer.flip()

            Client.instance.write(buffer)
        }
    }

    private fun init() {
        chatRooms = HashMap()
        users = ArrayList()
        chatRoomInfos = ArrayList()
        dataBuilder = NetData.Builder()
        Client.instance.setAddChatRoom(BiConsumer { roomId: String?, list:ArrayList<String> -> addChatRoom(roomId, list) })
    }

    private fun addChatRoom(roomId: String?, list : ArrayList<String>) {
        runOnUiThread {
            val info = ChatRoomInfo()
            info.apply {
                room_id = roomId
                user_ids = list
            }
            chatRoomInfos!!.add(info)
            listAdapter!!.notifyDataSetChanged()
        }
    }

    private fun intentChatRoom(chatRoomInfo: ChatRoomInfo) {
        val intent = Intent(this, ChatRoom::class.java)
        val bundle = Bundle()
        bundle.putSerializable("chatRoomInfo", chatRoomInfo)
        intent.putExtra("chatRoomInfo", bundle)
        startActivity(intent)
    }

    private fun deleteChatRoom() {}

    override fun onDestroy() {
        super.onDestroy()
        Client.instance.disconnect()
    }
}