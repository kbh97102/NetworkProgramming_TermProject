package com.example.network_termproject.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Client {

    private String ip = "192.168.200.122";
    private int port = 10101;
    private String name;
    private SocketChannel socket;
    private ExecutorService service;
    private Consumer<JSONObject> display;

    public Client() {
        service = Executors.newFixedThreadPool(10);

        service.execute(this::socketInit);
    }

    public void setDisplay(Consumer<JSONObject> display) {
        this.display = display;
    }

    private void socketInit() {
        try {
            socket = SocketChannel.open(new InetSocketAddress(ip, port));

            service.execute(this::read);
        } catch (IOException e) {
            System.out.println("Socket open Error");
            e.printStackTrace();
        }
    }

    /*
    타입에 따라 형식 변경해야됨
    1. 텍스트
    2. 이미지 (이모니콘)
    3. 파일
    4. 음성
     */
    public void write(String msg) {

        service.execute(() -> {
            try {
                System.out.println("in");
                ByteBuffer buffer = ByteBuffer.wrap("client".getBytes());
//                buffer.flip();
                socket.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Runnable read() {
        while (true) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(100000);
                socket.read(buffer);
                buffer.flip();
                byte[] arr = new byte[buffer.limit()];
                JSONObject data = new JSONObject(new String(arr));
                //화면에 반영 하기만 하면 됨
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
