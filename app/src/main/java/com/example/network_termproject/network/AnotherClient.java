package com.example.network_termproject.network;

import android.os.Parcel;
import android.os.Parcelable;

public class AnotherClient {

    private String name;

    public AnotherClient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
