package com.tuneit.streamsofmatches;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

public class AllRequest{

    private OkHttpClient okHttpClient;

    AllParamsForRequest allParamsForRequest;

    private int finishInsertMethod;
    private boolean bindFinish = false;

    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    AllRequest(String access_token, OkHttpClient okHttpClient, AllParamsForRequest allParamsForRequest){
        if(allParamsForRequest == null) {
            this.allParamsForRequest = new AllParamsForRequest();
        }else{
            this.allParamsForRequest = allParamsForRequest;
        }
        this.okHttpClient = okHttpClient;
        this.allParamsForRequest.setYOUR_API_KEY(access_token);
    }


    public AllParamsForRequest getAllParamsForRequest(){
        return allParamsForRequest;
    }

//    public boolean streamIsActive(){
//        if(streamStatus.equals("active")){ return true;}
//        else{return false;}
//    }
//
    public void startStream(){
//        streamStatus = streamList();
//       while(streamStatus != "active"){
//           streamStatus = streamList();
//       }
//        broadcastTransition("testing");
//        broadcastTransition("live");
    }


    public int getfinishInsertMethod(){
        return finishInsertMethod;
    }

    public void setfinishInsertMethod(int newVal){
        finishInsertMethod = newVal;
    }

    public boolean bindIsFinish(){
        return bindFinish;
    }



    public void broadcastInsert(String nameBroadcast, String time, String privacyStatus){
        allParamsForRequest.setCountBI(allParamsForRequest.getCountBI() + 1);
        System.out.println("Broadcast_insert");
        String json = "{\"snippet\":{\"scheduledStartTime\":\""+time+"\",\"title\":\""+nameBroadcast+"\"},\"status\":{\"privacyStatus\":\""+privacyStatus+"\"}}";
        RequestBody requestBody = RequestBody.create(JSON, json);

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveBroadcasts?part=id%2Csnippet%2CcontentDetails%2Cstatus&access_token="+allParamsForRequest.getYOUR_API_KEY())
                .post(requestBody)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("Broadcast_insert_FAIL", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String strResult = response.body().string();
                    JSONObject rootJsonObject = new JSONObject(strResult);
                    allParamsForRequest.setBroadcastId(rootJsonObject.get("id").toString());
//                    broadcastId = rootJsonObject.get("id").toString();

                    JSONObject snippetJsonObject = rootJsonObject.getJSONObject("snippet");
                    allParamsForRequest.setChatId(snippetJsonObject.get("liveChatId").toString());

                    Log.i("res in insert Broadcast", strResult);
                    finishInsertMethod++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void streamInsert(String nameStream, String ingestionType, String frameRate, String resolution){
        allParamsForRequest.setCountSI(allParamsForRequest.getCountSI() + 1);
        String json = "{\"cdn\": {\"ingestionType\": \""+ingestionType+"\",\"frameRate\": \""+frameRate+"\",\"resolution\": \""+resolution+"\"},\"snippet\": {\"title\": \""+nameStream+"\"}}";
        RequestBody requestBody = RequestBody.create(JSON, json);

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveStreams?part=id%2Csnippet%2Ccdn%2Cstatus&access_token=" + allParamsForRequest.getYOUR_API_KEY())
                .post(requestBody)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("Stream_insert_FAIL", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String strResult = response.body().string();
                    JSONObject rootJsonObject = new JSONObject(strResult);
//                    streamId = rootJsonObject.get("id").toString();
                    allParamsForRequest.setStreamId(rootJsonObject.get("id").toString());

                    JSONObject cdnJsonObject = rootJsonObject.getJSONObject("cdn");
                    JSONObject ingestionInfoJsonObject = cdnJsonObject.getJSONObject("ingestionInfo");

                    String streamName = ingestionInfoJsonObject.getString("streamName");
                    String ingestionAddress = ingestionInfoJsonObject.getString("ingestionAddress");
//                    rtmpUrl = ingestionAddress + "/" + streamName;
                    allParamsForRequest.setRtmpUrl(ingestionAddress + "/" + streamName);

                    Log.i("rtmp_url", allParamsForRequest.getRtmpUrl());
                    Log.i("res in insert Stream", strResult);
                    finishInsertMethod++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void broadcastBind(){
        allParamsForRequest.setCountBB(allParamsForRequest.getCountBB() + 1);
        RequestBody requestBody = new FormEncodingBuilder()
                .add("streamId", allParamsForRequest.getStreamId())
                .build();

        final com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveBroadcasts/bind?id=" + allParamsForRequest.getBroadcastId() + "&part=status&access_token=" + allParamsForRequest.getYOUR_API_KEY())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("Broadcast_bind_FAIL", e.toString());
                bindFinish = true;
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String str = response.body().string();
                Log.i("Broadcast_bind", str);
                bindFinish = true;
            }
        });
    }



    public void streamList(){
        allParamsForRequest.setCountSL(allParamsForRequest.getCountSL() + 1);
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveStreams?part=status&id="+allParamsForRequest.getStreamId()+"&access_token=" + allParamsForRequest.getYOUR_API_KEY())
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("Stream_list_FAIL", e.toString());
                allParamsForRequest.setStreamStatus("fail_request");
//                streamStatus = "fail";
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String str = response.body().string();
                    JSONObject rootJsonObject = new JSONObject(str);
                    Log.i("YouKey", allParamsForRequest.getYOUR_API_KEY());
                    Log.i("Stream_list", str);
                    JSONArray itemsJsonArray = rootJsonObject.getJSONArray("items");
                    JSONObject itemsJsonObject = itemsJsonArray.getJSONObject(0);
                    JSONObject statusJsonObject = itemsJsonObject.getJSONObject("status");
                    allParamsForRequest.setStreamStatus(statusJsonObject.getString("streamStatus"));
//                    streamStatus = statusJsonObject.getString("streamStatus");

//                    Log.i("status", streamStatus);

                } catch (JSONException e) {
                    e.printStackTrace();
                    allParamsForRequest.setStreamStatus("err_in_parse_json");
//                    streamStatus = "fail";
                }
            }
        });
    }

    private void broadcastList(){
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveBroadcasts?part=status&id="+allParamsForRequest.getBroadcastId()+"&access_token=" + allParamsForRequest.getYOUR_API_KEY())
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("Broadcast_list_FAIL", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String str = response.body().string();
                    JSONObject rootJsonObject = new JSONObject(str);
                    Log.i("Broadcast_list", str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void broadcastTransition(String broadcastStatus){
        if(broadcastStatus.intern() == "live"){
            allParamsForRequest.setCountBTL(allParamsForRequest.getCountBTL() + 1);
        }
        else{
            allParamsForRequest.setCountBTT(allParamsForRequest.getCountBTT() + 1);
        }

        RequestBody requestBody = new FormEncodingBuilder()
                .add("broadcastStatus", broadcastStatus)
                .add("id", allParamsForRequest.getBroadcastId())
                .build();

        final com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveBroadcasts/transition?&part=id%2Csnippet%2CcontentDetails%2Cstatus&access_token=" + allParamsForRequest.getYOUR_API_KEY())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("Broadcast_transit_FAIL", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String strResult = response.body().string();
                    JSONObject rootJsonObject = new JSONObject(strResult);
                    String resultIdBroadcast = rootJsonObject.get("id").toString();
                    if(resultIdBroadcast.intern() == allParamsForRequest.getBroadcastId().intern()){ allParamsForRequest.setBroadcastAlready(true); }
                    else{ allParamsForRequest.setBroadcastAlready(false); }

                    Log.i("res in insert Broadcast", strResult);
                } catch (JSONException e) {
                    allParamsForRequest.setBroadcastAlready(false);
                    e.printStackTrace();
                }


            }
        });
    }



    public void chatMessagesInsert(String liveChatId, String message){
        String json = "{\"snippet\":{\"type\":\"textMessageEvent\",\"textMessageDetails\":{\"messageText\":\""+message+"\"},\"liveChatId\":\""+liveChatId+"\"}}";

/*
        "{\"snippet\":{\"type\":\"textMessageEvent\",\"textMessageDetails\":{\"messageText\":\"Mdyaaaa\"},\"liveChatId\":\""+null+"\"}}"   // me
        "{\"snippet\":{\"type\":\"textMessageEvent\",\"textMessageDetails\":{\"messageText\":\"Mdyaaaa\"},\"liveChatId\":\""+null+"\"}}"
*/

        RequestBody requestBody = RequestBody.create(JSON, json);

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/liveChat/messages?part=snippet&access_token=" + allParamsForRequest.getYOUR_API_KEY())
                .post(requestBody)
                .addHeader("content-type", "application/json; charset=utf-8")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final com.squareup.okhttp.Request request, final IOException e) {
                Log.e("ChatMess_insert_FAIL", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    String strResult = response.body().string();
                    JSONObject rootJsonObject = new JSONObject(strResult);
                    Log.i("res mess insert", strResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
