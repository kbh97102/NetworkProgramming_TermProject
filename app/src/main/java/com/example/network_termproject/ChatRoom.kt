package com.example.network_termproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.network_termproject.network.Client
import com.example.network_termproject.network.NetData
import com.example.network_termproject.recycler.ChatAdapter
import kotlinx.android.synthetic.main.chat_room_layout.*
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Consumer


@RequiresApi(Build.VERSION_CODES.P)
class ChatRoom : AppCompatActivity() {

    private val galleryRequestCode = 30
    private var chatAdapter: ChatAdapter? = null
    private var datas: ArrayList<NetData>? = null
    private var dataBuilder: NetData.Builder? = null
    private var chatRoomInfo: ChatRoomInfo? = null
    private var imm: InputMethodManager? = null
    private var isEmojiSelected = false
    private val saver = TalkDataSaver()
    private var talkSaveFile: File? = null
    private lateinit var chatDistributor: ChatDistributor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_room_layout)

        init()

        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        val bundle = intent.getBundleExtra("chatRoomInfo")
        chatRoomInfo = bundle?.getSerializable("chatRoomInfo") as ChatRoomInfo?

        chatDistributor = ChatDistributor()
        chatDistributor.apply {
            init(getOutputDirectory(), this@ChatRoom::display)
            updateCurrentChatRoom(chatRoomInfo!!.room_id!!)
        }
        
//        Client.instance.setDisplay(chatDistributor::distribute)

        Client.instance.chatDistributor = this@ChatRoom.chatDistributor

        talkSaveFile = File(getOutputDirectory(), "${chatRoomInfo!!.room_id!!}1.txt")
        if (talkSaveFile!!.exists()) {
            val reader = FileReader(talkSaveFile)
            val bufferedReader = BufferedReader(reader)
            val iterator = bufferedReader.lineSequence().iterator()
            while (iterator.hasNext()) {
                val saveData = saver.parseToNetData(iterator.next())
                if (Objects.nonNull(saveData)){
                    datas!!.add(saveData!!)
                }
            }
            bufferedReader.close()
        }

        setButtonEvent()

        chat_room_toolbar.title = chatRoomInfo!!.room_id

        chatAdapter = ChatAdapter(datas!!, Client.instance.name, supportFragmentManager)
        chat_room_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatRoom)
            adapter = chatAdapter
        }
        chatAdapter!!.notifyDataSetChanged()
    }

    private fun setButtonEvent() {
        icon_2.setOnClickListener {
            val baos = ByteArrayOutputStream()
            val bitmap = (icon_2.drawable as BitmapDrawable).bitmap
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
        chat_room_sendButton.setOnClickListener {

            if (isEmojiSelected) {
                val clientData = dataBuilder!!
                        .setName(Client.instance.name)
                        .setUserId(Client.instance.id!!)
                        .setRoomId(chatRoomInfo!!.room_id!!)
                        .build()
                datas!!.add(clientData)
                val header = ByteBuffer.allocate(6)
                header.apply {
                    putChar('s')
                    putInt(clientData.data.toString().toByteArray().size)
                    flip()
                }

                var buffer = ByteBuffer.allocate(6 + clientData.data.toString().toByteArray().size)
                buffer.apply {
                    put(header)
                    put(clientData.data.toString().toByteArray())
                }
                buffer.flip()

                Client.instance.write(buffer)

                chatAdapter!!.notifyDataSetChanged()
                isEmojiSelected = false
                emoji_container.apply {
                    visibility = View.INVISIBLE
                    isFocusable = false
                }
            } else {
                val clientData = dataBuilder!!.setType("text")
                        .setName(Client.instance.name)
                        .setUserId(Client.instance.id!!)
                        .setRoomId(chatRoomInfo!!.room_id!!)
                        .setContent(chat_room_editText.text.toString())
                        .build()
                datas!!.add(clientData)
                val header = ByteBuffer.allocate(6)
                header.apply {
                    putChar('s')
                    putInt(clientData.data.toString().toByteArray().size)
                    flip()
                }
                var buffer = ByteBuffer.allocate(6 + clientData.data.toString().toByteArray().size)
                buffer.apply {
                    put(header)
                    put(clientData.data.toString().toByteArray())
                }
                buffer.flip()

                Client.instance.write(buffer)
                chatAdapter!!.notifyDataSetChanged()
                chat_room_editText.setText("")
            }
        }
        chat_room_emoji_button.setOnClickListener {
            if (imm!!.isAcceptingText) {
                hideKeyboard()
            }
            if (emoji_container.visibility == View.INVISIBLE) {
                emoji_container.apply {
                    visibility = View.VISIBLE
                    isFocusable = true
                }
            } else {
                emoji_container.apply {
                    visibility = View.INVISIBLE
                    isFocusable = false
                }
            }
        }
        chat_room_image_button.setOnClickListener {
            getImageFromGallery()
        }
    }

    private fun hideKeyboard() {
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun init() {
        datas = ArrayList()
        dataBuilder = NetData.Builder()
    }

    private fun display(data: NetData) {
        runOnUiThread {
            datas!!.add(data)
            chatAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        val fileWriter = FileWriter(talkSaveFile, true)
        val bufferedWriter = BufferedWriter(fileWriter)
        for (data in datas!!.iterator()) {
            if(data.getType() != "image"){
                bufferedWriter.write(saver.generate(data.getName(), data.getUserId(), data.getType(), data.getContent()))
                bufferedWriter.newLine()
            }
        }

        bufferedWriter.apply {
            flush()
            close()
        }

        Client.instance.chatDistributor!!.updateCurrentChatRoom(null)
        finish()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun getImageFromGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(intent, galleryRequestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == galleryRequestCode && resultCode == RESULT_OK && Objects.nonNull(data)) {
            val baos = ByteArrayOutputStream()
            val imageBitmap = ImageDecoder.createSource(this.contentResolver, data!!.data!!).let { ImageDecoder.decodeBitmap(it) }
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 95, baos)
            val imageData = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            val clientData = dataBuilder!!.setName(Client.instance.name)
                    .setType("image")
                    .setContent(imageData)
                    .setRoomId(chatRoomInfo!!.room_id!!)
                    .setUserId(Client.instance.id!!)
                    .build()

            datas!!.add(clientData)
            baos.close()

            val header = ByteBuffer.allocate(6)
            header.apply {
                putChar('s')
                putInt(clientData.data.toString().toByteArray().size)
                flip()
            }

            var buffer = ByteBuffer.allocate(6 + clientData.data.toString().toByteArray().size)
            buffer.apply {
                put(header)
                put(clientData.data.toString().toByteArray())
            }
            buffer.flip()

            Client.instance.write(buffer)

            chatAdapter!!.notifyDataSetChanged()
        }
    }
}