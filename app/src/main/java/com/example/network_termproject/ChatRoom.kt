package com.example.network_termproject

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network_termproject.databinding.ChatRoomLayoutBinding
import com.example.network_termproject.network.Client
import com.example.network_termproject.network.NetData
import com.example.network_termproject.recycler.ChatAdapter
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Consumer

class ChatRoom : AppCompatActivity() {
    /*
    채팅방이 가져야할 정보들
    1. 사용자 그룹 (1:1) or 그룹 -> arraylist쓰자
    3. 카톡 내용들 -> 내부 저장소에 캐시 형태로 저장하면 될 것 같다.
     */
    private val talkDataPath: String? = null
    val roomName: String? = null
    private var binding: ChatRoomLayoutBinding? = null
    private var chatAdapter: ChatAdapter? = null
    private var datas: ArrayList<NetData>? = null
    private var dataBuilder: NetData.Builder? = null
    private var chatRoomInfo: ChatRoomInfo? = null
    private var imm: InputMethodManager? = null
    var rootHeight = -1
    private var isEmojiSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatRoomLayoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

//
//        binding.chatRoomRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (rootHeight == -1) {
//                    rootHeight = binding.chatRoomRootView.getHeight();
//                }
//                Rect visibleFrameSize = new Rect();
//                binding.chatRoomRootView.getWindowVisibleDisplayFrame(visibleFrameSize);
//                int heightExceptKeyboard = visibleFrameSize.bottom - visibleFrameSize.top;
//                if (heightExceptKeyboard < rootHeight) {
//                    int keyboardHeight = rootHeight - heightExceptKeyboard;
//                    binding.emojiContainer.setMaxHeight(keyboardHeight);
//                }
//            }
//        });
        val bundle = intent.getBundleExtra("chatRoomInfo")!!
        chatRoomInfo = bundle.getSerializable("chatRoomInfo") as ChatRoomInfo?
        init()
        binding!!.chatRoomToolbar.title = chatRoomInfo!!.room_id
        binding!!.icon2.setOnClickListener {
            val baos = ByteArrayOutputStream()
            val bitmap = (binding!!.icon2.drawable as BitmapDrawable).bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, baos)
            //            dataBuilder.setType("image").setImageData(baos);
            val imageData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            dataBuilder!!.setType("image").setImage(imageData)
            try {
                baos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            isEmojiSelected = true
        }
        chatAdapter = ChatAdapter(datas!!, Client.instance.name)
        binding!!.chatRoomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding!!.chatRoomRecyclerView.adapter = chatAdapter
        binding!!.chatRoomSendButton.setOnClickListener {

            //TODO 테스트용임 이모티콘 (이미지)보내기
            if (isEmojiSelected) {
                val clientData = dataBuilder!!
                        .setName(Client.instance.name)
                        .setUserId(Client.instance.id!!)
                        .setRoomId(chatRoomInfo!!.room_id!!)
                        .build()
                datas!!.add(clientData)
                val header = ByteBuffer.allocate(6)
                header.putChar('s')
                header.putInt(clientData.data.toString().toByteArray().size)
                header.flip()

                var buffer = ByteBuffer.allocate(6+clientData.data.toString().toByteArray().size)
                buffer.apply {
                    put(header)
                    put(clientData.data.toString().toByteArray())
                }
                buffer.flip()

                Client.instance.write(buffer)

                chatAdapter!!.notifyDataSetChanged()
                isEmojiSelected = false
                binding!!.emojiContainer.visibility = View.INVISIBLE
                binding!!.emojiContainer.isFocusable = false
            } else {
                val clientData = dataBuilder!!.setType("text")
                        .setName(Client.instance.name)
                        .setUserId(Client.instance.id!!)
                        .setRoomId(chatRoomInfo!!.room_id!!)
                        .setContent(binding!!.chatRoomEditText.text.toString())
                        .build()
                datas!!.add(clientData)
                val header = ByteBuffer.allocate(6)
                header.putChar('s')
                header.putInt(clientData.data.toString().toByteArray().size)
                header.flip()
                var buffer = ByteBuffer.allocate(6+clientData.data.toString().toByteArray().size)
                buffer.apply {
                    put(header)
                    put(clientData.data.toString().toByteArray())
                }
                buffer.flip()

                Client.instance.write(buffer)
                chatAdapter!!.notifyDataSetChanged()
                binding!!.chatRoomEditText.setText("")
            }
        }
        binding!!.chatRoomEmojiButton.setOnClickListener { view: View? ->
            if (imm!!.isAcceptingText) {
                hideKeyboard()
            }
            if (binding!!.emojiContainer.visibility == View.INVISIBLE) {
                binding!!.emojiContainer.visibility = View.VISIBLE
                binding!!.emojiContainer.isFocusable = true
            } else {
                binding!!.emojiContainer.visibility = View.INVISIBLE
                binding!!.emojiContainer.isFocusable = false
            }
        }
        chatAdapter!!.notifyDataSetChanged()
    }

    private fun hideKeyboard() {
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun init() {
        datas = ArrayList()
        Client.instance.setDisplay(Consumer { data: NetData? -> display(data!!) })
        //        Client.getInstance().setName("testName");
        dataBuilder = NetData.Builder()
    }

    private fun display(data: NetData) {
        runOnUiThread {
            datas!!.add(data)
            chatAdapter!!.notifyDataSetChanged()
        }
    }

}