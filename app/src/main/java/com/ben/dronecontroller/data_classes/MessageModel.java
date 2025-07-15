package com.ben.dronecontroller.data_classes;

import java.io.Serializable;

public class MessageModel implements Serializable {
    public String message;
    public String time;
    public int viewType;

    public MessageModel(String message, String time, int viewType){
        this.message = message;
        this.time = time;
        this.viewType = viewType;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public int getViewType() {
        return viewType;
    }
}
