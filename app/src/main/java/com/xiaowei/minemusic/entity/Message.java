package com.xiaowei.minemusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {

    private String content;

    private boolean isSendStatus;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeByte(this.isSendStatus ? (byte) 1 : (byte) 0);
    }

    public Message() {
    }

    protected Message(Parcel in) {
        this.content = in.readString();
        this.isSendStatus = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSendStatus() {
        return isSendStatus;
    }

    public void setSendStatus(boolean sendStatus) {
        isSendStatus = sendStatus;
    }

    public void readFromParcel(Parcel parcel){
        content = parcel.readString();
        //是否为true
        isSendStatus = parcel.readByte() == 1;
    }
}
