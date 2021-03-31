package com.example.network_termproject

import com.example.network_termproject.network.NetData
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

class ChatDistributor {

    private lateinit var executor :ExecutorService
    private var currentChatRoom : String? = null
    private var display: Consumer<NetData>? = null
    private lateinit var talkSaveFile : File
    private lateinit var outputDirectory : File
    private lateinit var saver : TalkDataSaver

    fun init(outputDirectory : File, display : Consumer<NetData>?){
        executor = Executors.newFixedThreadPool(5)
        this.outputDirectory = outputDirectory
        saver = TalkDataSaver()
        this.display = display
    }

    fun distribute(netData: NetData){
        if (Objects.nonNull(currentChatRoom) && currentChatRoom == netData.getRoomId()){
            display!!.accept(netData)
        }
        else{
            executor.execute {
                talkSaveFile = File(outputDirectory, "${netData.getRoomId()}1.txt")
                val fileWriter = FileWriter(talkSaveFile, true)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.apply {
                    write(saver.generate(netData.getName(), netData.getUserId(), netData.getType(), netData.getContent()))
                    newLine()
                    flush()
                    close()
                }
            }
        }
    }

    fun updateCurrentChatRoom(chatRoomId : String?){
        this.currentChatRoom = chatRoomId
    }
}