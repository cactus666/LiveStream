package com.tuneit.streamsofmatches;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.squareup.okhttp.OkHttpClient;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;




/*
Drop:
    mPublisher.startRecord(recPath)
    mPublisher.pauseRecord();
    mPublisher.resumeRecord();
*/


public class VideoEncoderActivity extends AppCompatActivity implements RtmpHandler.RtmpListener,
        SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

    private static final String TAG = "Yasea";
    private Bundle arguments;

    private MyAsyncTask myAsyncTask;
    private AsyncTaskForUpdateAccount asyncTaskForUpdateAccount;
    private Button btnPublish;
    private Button btnSwitchCamera;
    private Button countPlayer1_add;
    private Button countPlayer2_add;
    private Button countPlayer1_sub;
    private Button countPlayer2_sub;
//    private Button btnPause;

    private String format;
    private String rtmpUrl;
    private AllParamsForRequest allParamsForRequest;
    private AllRequest allRequest;

//    private SharedPreferences sp;

    private SrsPublisher mPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.encoder_video);
//        setContentView(R.layout.activity_main_for_test_lib);


        arguments = getIntent().getExtras();
        format = getIntent().getStringExtra("format");


        allParamsForRequest = (AllParamsForRequest) arguments.getSerializable("allRequestParamsForRequest");
        allRequest = new AllRequest(allParamsForRequest.getYOUR_API_KEY(), new OkHttpClient(), allParamsForRequest);

        rtmpUrl = allParamsForRequest.getRtmpUrl();

        // response screen rotation event - событие поворота экрана ответа
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        // restore data. - восстановить данные.
/*
        sp = getSharedPreferences("Yasea", MODE_PRIVATE);
        rtmpUrl = sp.getString("rtmpUrl", rtmpUrl);
*/

        // initialize url.
//        final EditText efu = (EditText) findViewById(R.id.url);
//        efu.setText(rtmpUrl);

        btnPublish = (Button) findViewById(R.id.publish);
        btnSwitchCamera = (Button) findViewById(R.id.swCam);
        countPlayer1_add = (Button) findViewById(R.id.countPlayer1_add);
        countPlayer2_add = (Button) findViewById(R.id.countPlayer2_add);
        countPlayer1_sub = (Button) findViewById(R.id.countPlayer1_sub);
        countPlayer2_sub = (Button) findViewById(R.id.countPlayer2_sub);

//        btnPause = (Button) findViewById(R.id.pause);
//        btnPause.setEnabled(false);

        mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewResolution(1280, 720);
        mPublisher.setOutputResolution(720, 1280);

        mPublisher.setVideoHDMode();
        mPublisher.startCamera();

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnPublish.getText().toString().contentEquals("publish")) {
                    /*
                    rtmpUrl = efu.getText().toString();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("rtmpUrl", rtmpUrl);
                    editor.apply();
                    */
                    mPublisher.startPublish(rtmpUrl);
                    mPublisher.startCamera();

                    btnPublish.setText("stop");
//                    btnPause.setEnabled(true);

                    myAsyncTask = new MyAsyncTask(allRequest,VideoEncoderActivity.this);
                    myAsyncTask.execute();

                    asyncTaskForUpdateAccount = new AsyncTaskForUpdateAccount(allRequest, getIntent().getStringExtra("player1"), getIntent().getStringExtra("player2"));
                    asyncTaskForUpdateAccount.execute();

                } else if (btnPublish.getText().toString().contentEquals("stop")) {
                    asyncTaskForUpdateAccount.cancel(true);
                    mPublisher.stopPublish();
                    mPublisher.stopRecord();
                    btnPublish.setText("publish");
//                    btnPause.setEnabled(false);

                    allRequest.broadcastTransition("complete");

                }
            }
        });
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(btnPause.getText().toString().equals("Pause")){
//                    mPublisher.pausePublish();
//                    btnPause.setText("resume");
//                }else{
//                    mPublisher.resumePublish();
//                    btnPause.setText("Pause");
//                }
//            }
//        });

        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublisher.switchCameraFace((mPublisher.getCameraId() + 1) % Camera.getNumberOfCameras());
            }
        });


        countPlayer1_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allParamsForRequest.setCountPlayer1(allParamsForRequest.getCountPlayer1() + 1);
            }
        });

        countPlayer2_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allParamsForRequest.setCountPlayer2(allParamsForRequest.getCountPlayer2() + 1);
            }
        });

        countPlayer1_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allParamsForRequest.setCountPlayer1(allParamsForRequest.getCountPlayer1() - 1);
            }
        });

        countPlayer2_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allParamsForRequest.setCountPlayer2(allParamsForRequest.getCountPlayer2() - 1);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Button btn = (Button) findViewById(R.id.publish);
        btn.setEnabled(true);
        mPublisher.resumeRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPublisher.pauseRecord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPublisher.stopEncode();
        mPublisher.stopRecord();
        mPublisher.setScreenOrientation(newConfig.orientation);
        if (btnPublish.getText().toString().contentEquals("stop")) {
            mPublisher.startEncode();
        }
        mPublisher.startCamera();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
            btnPublish.setText("publish");
        } catch (Exception e1) {
            //
        }
    }

    // Implementation of SrsRtmpListener.

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoStreaming() {
    }

    @Override
    public void onRtmpAudioStreaming() {
    }

    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    // Implementation of SrsRecordHandler.

    @Override
    public void onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordStarted(String msg) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordFinished(String msg) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    // Implementation of SrsEncodeHandler.

    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }
}


class MyAsyncTask extends AsyncTask<Void, Void, String> {
    AllRequest allRequest;
    int count = 0;
    Context context;

    public MyAsyncTask(AllRequest allRequest, Context context) {
        this.allRequest = allRequest;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... params) {
        allRequest.streamList();
        count++;
//        while((streamStatus == "") | (streamStatus.intern() != "active")){
        while((allRequest.getAllParamsForRequest().getStreamStatus() == null) || (allRequest.getAllParamsForRequest().getStreamStatus().intern() != "active")){
            if(count >= 10){return null;}
            if(allRequest.getAllParamsForRequest().getStreamStatus() != null){
                allRequest.streamList();
                count++;
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        count = 0;

        while(!allRequest.getAllParamsForRequest().isBroadcastAlready()){
            if(count >= 20){ return null; }
            else{
                allRequest.broadcastTransition("testing");
                count++;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        count = 0;
        allRequest.getAllParamsForRequest().setBroadcastAlready(false);

        while(!allRequest.getAllParamsForRequest().isBroadcastAlready()){
            if(count >= 20){ return null; }
            else{
                allRequest.broadcastTransition("live");
                count++;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return allRequest.getAllParamsForRequest().getStreamStatus();
    }

    @Override
    protected void onPostExecute(String result) {
        String all_count_result = allRequest.getAllParamsForRequest().getCountBI()+"_"+
            allRequest.getAllParamsForRequest().getCountSI()+"_"+
            allRequest.getAllParamsForRequest().getCountBB()+"_"+
            allRequest.getAllParamsForRequest().getCountSL()+"_"+
            allRequest.getAllParamsForRequest().getCountBTT()+"_"+
            allRequest.getAllParamsForRequest().getCountBTL();


        if(result == null){
            Toast.makeText(context, "FAIL___"+all_count_result, Toast.LENGTH_LONG).show();
//            Toast.makeText(context, "fail :(" + " - " + count, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, all_count_result, Toast.LENGTH_LONG).show();
//            Toast.makeText(context, "Start" + count, Toast.LENGTH_SHORT).show();

        }
    }
}

class AsyncTaskForUpdateAccount extends AsyncTask<Void, Integer, Void> {
    AllRequest allRequest;
    AllParamsForRequest allParamsForRequest;
    String player1, player2, message;

    public AsyncTaskForUpdateAccount(AllRequest allRequest, String player1, String player2) {
        this.allRequest = allRequest;
        allParamsForRequest = (AllParamsForRequest)allRequest.getAllParamsForRequest();
        this.player1 = player1;
        this.player2 = player2;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(true){
            if(isCancelled()){ return null; }
            if(allParamsForRequest.getCountPlayer1() > allParamsForRequest.getCountPlayer2()){
                message = player1 + " " + (allParamsForRequest.getCountPlayer1()) + " > " + (allParamsForRequest.getCountPlayer2()) + " " + player2;
            }else{
                message = player1 + " " + (allParamsForRequest.getCountPlayer1()) + " < " + (allParamsForRequest.getCountPlayer2()) + " " + player2;
            }

            allRequest.chatMessagesInsert(allParamsForRequest.getChatId(), message);
            try {
                TimeUnit.MINUTES.sleep(3);
            }catch(InterruptedException e){
                e.printStackTrace();
                return null;
            }
        }
//        return null;
    }

    @Override
    protected void onPostExecute(Void v) {}

    @Override
    protected void onCancelled(){
        super.onCancelled();
        // можно что-то сделать при выходе, например вызвать метод добавления сообщения в комментарии "Всем пока"
    }
}