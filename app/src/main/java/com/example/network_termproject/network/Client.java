package com.example.network_termproject.network;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Client {


    private String ip = "192.168.200.181";
    private int port = 10101;
    private String name = "testName";
    private String id;
    private SocketChannel socket;
    private Executor executor;
    private Consumer<NetData> display;
    private Consumer<NetData> setList;
    private Consumer<String> addChatRoom;
    private NetData.Builder clientDataBuilder;

    private static class ClientHolder {
        private static final Client instance = new Client();
    }

    private Client() {
        executor = Executors.newFixedThreadPool(10);
        clientDataBuilder = new NetData.Builder();
        executor.execute(this::socketInit);
//        service.execute(this::socketInit);
        name = "testName";
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Client getInstance() {
        return ClientHolder.instance;
    }

    public boolean testMethod(){
        return socket.isConnected();
    }

    public String getId() {
        return id;
    }

    public void setDisplay(Consumer<NetData> display) {
        this.display = display;
    }

    public void setList(Consumer<NetData> setList) {
        this.setList = setList;
    }

    private void socketInit() {
        try {
            socket = SocketChannel.open();
            connect();
        } catch (IOException e) {
            System.out.println("Socket open Error");
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket.connect(new InetSocketAddress(ip, port));
            read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void startRead() {
        executor.execute(this::read);
    }

    /*
    타입에 따라 형식 변경해야됨
    1. 텍스트
    2. 이미지 (이모니콘)
    3. 파일
    4. 음성
     */
    public void write(NetData data) {
        executor.execute(() -> {
            try {
                socket.write(data.parseByteBuffer());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void read() {
        executor.execute(() -> {
            while (true) {
                try {
                    ByteBuffer buffer = ByteBuffer.allocate(100000);
                    socket.read(buffer);
                    buffer.flip();
                    byte[] arr = new byte[buffer.limit()];
                    buffer.get(arr, 0, buffer.limit());
                    Log.d("Server data", clientDataBuilder.parseServerData(new String(arr)).getData().toString());
                    doWorkWithType(clientDataBuilder.parseServerData(new String(arr)));
                    //화면에 반영 하기만 하면 됨
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void doWorkWithType(NetData serverData) {
        if (serverData.getType().equals("requestList")) {
            setList.accept(serverData);
        } else if (serverData.getType().equals("clientId")) {
            this.id = serverData.getContent();
        } else if (serverData.getType().equals("requestAdd")) {
            addChatRoom.accept(serverData.getContent());
        } else {
            display.accept(serverData);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddChatRoom(Consumer<String> addChatRoom) {
        this.addChatRoom = addChatRoom;
    }
}
