package com.tuneit.streamsofmatches;

import com.squareup.okhttp.OkHttpClient;

import java.io.Serializable;

public class AllParamsForRequest implements Serializable{

    private String YOUR_API_KEY;
    private String broadcastId;
    private String streamId;
    private String chatId;
    private String rtmpUrl;
    private String streamStatus = null;
    private boolean broadcastAlready = false;

    // ? точно тут или можно засунуть это кудато поэлегантнее
    private int countPlayer1 = 0;
    private int countPlayer2 = 0;


    private int countBI = 0;
    private int countSI = 0;
    private int countBB = 0;
    private int countSL = 0;
    private int countBTT = 0;
    private int countBTL = 0;

    public int getCountPlayer1() {
        return countPlayer1;
    }

    public void setCountPlayer1(int countPlayer1) {
        this.countPlayer1 = countPlayer1;
    }

    public int getCountPlayer2() {
        return countPlayer2;
    }

    public void setCountPlayer2(int countPlayer2) {
        this.countPlayer2 = countPlayer2;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public int getCountBI() {
        return countBI;
    }

    public void setCountBI(int countBI) {
        this.countBI = countBI;
    }

    public int getCountSI() {
        return countSI;
    }

    public void setCountSI(int countSI) {
        this.countSI = countSI;
    }

    public int getCountBB() {
        return countBB;
    }

    public void setCountBB(int countBB) {
        this.countBB = countBB;
    }

    public int getCountSL() {
        return countSL;
    }

    public void setCountSL(int countSL) {
        this.countSL = countSL;
    }

    public int getCountBTT() {
        return countBTT;
    }

    public void setCountBTT(int countBTT) {
        this.countBTT = countBTT;
    }

    public int getCountBTL() {
        return countBTL;
    }

    public void setCountBTL(int countBTL) {
        this.countBTL = countBTL;
    }

    public boolean isBroadcastAlready() {
        return broadcastAlready;
    }

    public void setBroadcastAlready(boolean broadcastAlready) {
        this.broadcastAlready = broadcastAlready;
    }

    public String getYOUR_API_KEY() {
        return YOUR_API_KEY;
    }

    public void setYOUR_API_KEY(String YOUR_API_KEY) {
        this.YOUR_API_KEY = YOUR_API_KEY;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public String getStreamStatus() {
        return streamStatus;
    }

    public void setStreamStatus(String streamStatus) {
        this.streamStatus = streamStatus;
    }

}
