package com.example.network_termproject.network

import android.util.Log
import com.example.network_termproject.ChatDistributor
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.Consumer

class Client private constructor() {
    //notebook 120
    //pc 181
    private val ip = "192.168.200.120"
    private val port = 10101
    var name = "x1"
    var id: String? = null
    private var socket: SocketChannel? = null
    private var executor: ExecutorService = Executors.newFixedThreadPool(10)
    private var setList: Consumer<NetData>? = null
    private var addChatRoom: BiConsumer<String, ArrayList<String>>? = null
    private val clientDataBuilder: NetData.Builder = NetData.Builder()
    var chatDistributor: ChatDistributor? = null
    private var rootDirectory: File? = null

    private object ClientHolder {
        val instance = Client()
    }

    fun setList(setList: Consumer<NetData>?) {
        this.setList = setList
    }

    fun setRoot(file: File) {
        rootDirectory = file
    }

    private fun socketInit() {
        try {
            socket = SocketChannel.open()
            connect()
        } catch (e: IOException) {
            println("Socket open Error")
            e.printStackTrace()
        }
    }

    private fun connect() {
        try {
            socket!!.connect(InetSocketAddress(ip, port))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun startRead() {
        executor.execute { read() }
    }

    fun write(data: NetData?) {
        executor.execute {
            try {
                socket!!.write(data!!.parseByteBuffer())
            } catch (e: IOException) {
                disconnect()
                e.printStackTrace()
            }
        }
    }

    fun write(headerBuffer: ByteBuffer?) {
        executor.execute {
            try {
                socket!!.write(headerBuffer)
            } catch (e: IOException) {
                disconnect()
                e.printStackTrace()
            }
        }
    }

    private fun read() {
        executor.execute {
            while (true) {
                try {
                    val buffer = ByteBuffer.allocate(6)
                    val rect = socket!!.read(buffer)
                    if (rect <= 1) {
                        return@execute
                    }
                    buffer.flip()
                    val c = buffer.char
                    val size = buffer.int
                    val dataBuffer = ByteBuffer.allocate(size)
                    while (dataBuffer.hasRemaining()) {
                        socket!!.read(dataBuffer)
                    }

                    val receivedData = clientDataBuilder.parseServerData(String(dataBuffer.array()))
                    doWorkWithType(receivedData)
                } catch (e: IOException) {
                    disconnect()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun doWorkWithType(serverData: NetData) {
        if (Objects.isNull(serverData)) {
            return
        }
        if (Objects.isNull(chatDistributor)) {
            chatDistributor = ChatDistributor()
            chatDistributor!!.init(rootDirectory!!, null)
        }
        when {
            serverData.getType() == "requestList" -> {
                setList!!.accept(serverData)
            }
            serverData.getType() == "clientId" -> {
                id = serverData.getContent()
            }
            serverData.getType() == "requestAdd" -> {
                addChatRoom!!.accept(serverData.getContent(), serverData.getList())
            }
            serverData.getType() == "image" -> {
                chatDistributor!!.distribute(serverData)
            }
            else -> {
                chatDistributor!!.distribute(serverData)
            }
        }
    }

    fun setAddChatRoom(addChatRoom: BiConsumer<String, ArrayList<String>>?) {
        this.addChatRoom = addChatRoom
    }

    fun disconnect() {
        write(clientDataBuilder.setType("disconnect").setUserId(id!!).build())
        executor.shutdown()
        try {
            if (!executor.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }
    }

    companion object {
        val instance: Client
            get() = ClientHolder.instance
    }

    init {
        executor.execute { socketInit() }
    }

}