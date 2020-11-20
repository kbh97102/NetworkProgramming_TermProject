package com.example.network_termproject.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class NetData {

    private JSONObject data;

    private NetData(){

    }

    public static class Builder{

        private String name = "no";
        private String userId;
        private String type;
        private String content;
        private String chatRoomId;
        private JSONArray list;

        public Builder(){
            list = new JSONArray();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setList(ArrayList<String> dataList) {
            for (String item : dataList){
                this.list.put(item);
            }
            return this;
        }

        public Builder setRoomId(String id){
            this.chatRoomId = id;
            return this;
        }

        public Builder setUserId(String id){
            this.userId = id;
            return this;
        }

        public NetData build() {
            NetData clientData = new NetData();
            clientData.data = new JSONObject();
            try{
                if (name.equals("no")){
                    name = Client.getInstance().getName();
                }
                clientData.data.put("name", name);
                clientData.data.put("type", type);
                clientData.data.put("content", content);
                clientData.data.put("list", list);
                clientData.data.put("roomId", chatRoomId);
                clientData.data.put("userId", userId);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return clientData;
        }


        public NetData parseServerData(String data)  {
            NetData receivedData = new NetData();
            try{
                receivedData.data = new JSONObject(data);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return receivedData;
        }

        public NetData buildList(){
            NetData clientData = new NetData();
            clientData.data = new JSONObject();
            try{
                clientData.data.put("name", name);
                clientData.data.put("type", type);
                clientData.data.put("content", list);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return clientData;
        }
    }

    public JSONObject getData(){
        return data;
    }

    public String getType(){
        try {
            return (String) data.get("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName(){
        try {
            return (String) data.get("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getContent(){
        try {
            return (String) data.get("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserId(){
        try {
            return (String) data.get("userId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRoomId(){
        try {
            return (String) data.get("roomId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getList(){
        try {
            JSONArray array = (JSONArray) data.get("list");
            ArrayList<String> arr = new ArrayList<>();
            for (int i=0;i<array.length();i++){
                String test = array.getString(i).replace("[", "").replace("]","");
                arr.add(test);
            }
            return arr;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getImage(){
        try {
            byte[] imageData = Base64.decode(data.getString("content"), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ByteBuffer parseByteBuffer(){
        return ByteBuffer.wrap(data.toString().getBytes());
    }
}
