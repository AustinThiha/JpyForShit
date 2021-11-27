package chem.kth.spyforlove.model;

import chem.kth.spyforlove.MessageType;

public class SMessage {
    private int id;
    private String phoneNo;
    private String body;
    private String date;
    private MessageType messageType;

    public SMessage(int id, String phoneNo, String body, String date, MessageType messageType) {
        this.id = id;
        this.phoneNo = phoneNo;
        this.body = body;
        this.date = date;
        this.messageType = messageType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "SMessage{" +
                "id=" + id +
                ", phoneNo='" + phoneNo + '\'' +
                ", body='" + body + '\'' +
                ", date='" + date + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
