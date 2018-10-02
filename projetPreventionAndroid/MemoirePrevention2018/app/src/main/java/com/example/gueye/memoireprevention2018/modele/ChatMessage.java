package com.example.gueye.memoireprevention2018.modele;

import java.util.Date;

public class ChatMessage {

    private String receiver;
    private String sender;
    private String message;

    public ChatMessage(String receiver, String sender, String message, Date timeSend) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;

    }

    public ChatMessage() {
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
