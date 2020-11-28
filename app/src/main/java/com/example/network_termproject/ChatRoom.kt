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
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class ChatRoom : AppCompatActivity() {

    private var binding: ChatRoomLayoutBinding? = null
    private var chatAdapter: ChatAdapter? = null
    private var datas: ArrayList<NetData>? = null
    private var dataBuilder: NetData.Builder? = null
    private var chatRoomInfo: ChatRoomInfo? = null
    private var imm: InputMethodManager? = null
    private var isEmojiSelected = false
    private val saver = TalkDataSaver()
    private var talkSaveFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatRoomLayoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        init()

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        val bundle = intent.getBundleExtra("chatRoomInfo")
        chatRoomInfo = bundle?.getSerializable("chatRoomInfo") as ChatRoomInfo?

        talkSaveFile = File(getOutputDirectory(), "${chatRoomInfo!!.room_id!!}1.txt")
        if (talkSaveFile!!.exists()) {
            val reader = FileReader(talkSaveFile)
            val bufferedReader = BufferedReader(reader)
            val iterator = bufferedReader.lineSequence().iterator()
            while(iterator.hasNext()){
                datas!!.add(saver.parseToNetData(iterator.next()))
            }
            bufferedReader.close()
        }

        setButtonEvent()

        binding?.chatRoomToolbar?.title = chatRoomInfo!!.room_id

        chatAdapter = ChatAdapter(datas!!, Client.instance.name)
        binding!!.chatRoomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding!!.chatRoomRecyclerView.adapter = chatAdapter

        chatAdapter!!.notifyDataSetChanged()
    }

    private fun setButtonEvent() {
        binding?.icon2?.setOnClickListener {
            val baos = ByteArrayOutputStream()
            val bitmap = (binding!!.icon2.drawable as BitmapDrawable).bitmap
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, baos)
            val imageData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            dataBuilder!!.setType("image").setContent(imageData)
            try {
                baos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            isEmojiSelected = true
        }
        binding!!.chatRoomSendButton.setOnClickListener {

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

                var buffer = ByteBuffer.allocate(6 + clientData.data.toString().toByteArray().size)
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
                var buffer = ByteBuffer.allocate(6 + clientData.data.toString().toByteArray().size)
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
    }

    private fun hideKeyboard() {
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun init() {
        datas = ArrayList()
        Client.instance.setDisplay(Consumer { data: NetData? -> display(data!!) })
        dataBuilder = NetData.Builder()
    }

    private fun display(data: NetData) {
        runOnUiThread {
            datas!!.add(data)
            chatAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {

        val fileWriter = FileWriter(talkSaveFile)
        val bufferedWriter = BufferedWriter(fileWriter)
        for (data in datas!!.iterator()) {
            bufferedWriter.write(saver.generate(data.getName(), data.getUserId(), data.getType(), data.getContent()))
            bufferedWriter.newLine()
        }

        bufferedWriter.apply {
            flush()
            close()
        }

        finish()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}