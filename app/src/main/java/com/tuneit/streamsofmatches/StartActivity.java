package com.tuneit.streamsofmatches;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StartActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private EditText nameStreamET;
    private EditText player1ET;
    private EditText player2ET;
    private EditText formatET;
    private Button startB;
    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeButton;
    private TextView mResult;
    //    private AuthGoogle authGoogle;
    private String token;
    private static final int SIGNED_IN = 0;
    private static final int STATE_SIGNING_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final int RC_SIGN_IN = 0;
    private static final int RC_ENCOD_VIDEO = 5;
    private GoogleApiClient mGoogleApiClient;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;
    private OkHttpClient client = new OkHttpClient();
    private AllRequest allRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        new LinearLayout(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_options);

        nameStreamET = (EditText) findViewById(R.id.name_stream);
        player1ET = (EditText) findViewById(R.id.player1);
        player2ET = (EditText) findViewById(R.id.player2);
        formatET = (EditText) findViewById(R.id.format);
        mResult = (TextView) findViewById(R.id.result);

        startB = (Button) findViewById(R.id.start);
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mRevokeButton = (Button) findViewById(R.id.revoke_access_button);
        // Add click listeners for the buttons
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
        mRevokeButton.setOnClickListener(this);
        startB.setOnClickListener(this);

        // Build a GoogleApiClient
        mGoogleApiClient = buildGoogleApiClient();
    }

    private GoogleApiClient buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id),false)
//                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
//                .requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ))//.DRIVE_APPFOLDER))
//                .requestScopes(new Scope("https://www.googleapis.com/auth/youtube.readonly"))//upload"))//readonly"))
                .build();


// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        return new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    /*
    Когда активити запускается наступает лучшее время для подключения к Google Play services.
    А когда останавливается - лучшее время для отключения.
    */
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = result.getResolution();
            if (mSignInProgress == STATE_SIGNING_IN) {
                resolveSignInError();
            }
        }
        // Will implement shortly
        onSignedOut();
    }

    private void resolveSignInError() {
        Log.e("error", "error in resolveSignInError method");
//        if (mSignInIntent != null) {
//            try {
//                mSignInProgress = STATE_IN_PROGRESS;
//                mConnectionResult.startResolutionForResult(this, OUR_REQUEST_CODE);
//            } catch (IntentSender.SendIntentException e) {
//                mSignInProgress = STATE_SIGNING_IN;
//                mGoogleApiClient.connect();
//            }
//        } else {
//            // You have a play services error -- inform the user
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult");
        System.out.println("resultCode = "+resultCode);

//        if(resultCode == RESULT_OK){
        switch (requestCode) {
            case RC_SIGN_IN:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                mResult.setText(result.isSuccess()+" - "+result.getStatus().getStatusCode());
                System.out.println("result.isSuccess - "+result.isSuccess()+".");
                System.out.println("result -"+ result.getSignInAccount()+".");
                System.out.println("ststus = " + result.getStatus().getStatusCode());

                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    String authCode = account.getServerAuthCode();
                    System.out.println("authcode"+authCode);
                    getAccessToken(authCode);
                }


                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
            case RC_ENCOD_VIDEO:
                break;
        }
//        }else{
//            System.out.println("Fail in onActivityResult");
//        }


//        switch (requestCode) {
//            case RC_SIGN_IN:
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                if (result.isSuccess()) {
//                    GoogleSignInAccount account = result.getSignInAccount();
//                    String authCode = account.getServerAuthCode();
//                    System.out.println("authcode"+authCode);
//                    getAccessToken(authCode);
//                }
//
//                if (resultCode == RESULT_OK) {
//                    mSignInProgress = STATE_SIGNING_IN;
//                } else {
//                    mSignInProgress = SIGNED_IN;
//                }
//
//                if (!mGoogleApiClient.isConnecting()) {
//                    mGoogleApiClient.connect();
//                }
//                break;
//        }
    }

    private void onSignedOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // Update the UI to reflect that the user is signed out.
                        mSignInButton.setEnabled(true);
                        mSignOutButton.setEnabled(false);
                        mRevokeButton.setEnabled(false);
                    }
                });
    }

    public void getAccessToken(String authCode) {
        RequestBody requestBody = new FormEncodingBuilder() //new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", getString(R.string.server_client_id))
                .add("client_secret", getString(R.string.client_secret))
//                .add("redirect_uri", "")
//                .add("approval_prompt", "force")
//                .add("access_type","offline")
                .add("code", authCode)
                .build();

        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                Log.e("TAG", e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String message = jsonObject.toString();
                    Log.i("TAG2", message);
                    token = jsonObject.get("access_token").toString();
                    System.out.println(message);
                    System.out.println(response);
                    allRequest = new AllRequest(token, client, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//                    setResult(RESULT_OK);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    break;
                case R.id.sign_out_button:
                    onSignedOut();
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    token = null;
                    break;
                case R.id.revoke_access_button:
                    Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
                    mGoogleApiClient = buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    token = null;
                    break;
                case R.id.start:
                    java.util.Date date = Calendar.getInstance().getTime();

                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:");
                    DateFormat df2 = new SimpleDateFormat("mm");
                    String time = df.format(date) + (Integer.parseInt(df2.format(date)) + 1) + ":00+03";

//                    DateFormat df3 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                    String time2 = df3.format(date);

//                  // (YYYY-MM-DDThh:mm:ss.sZ) (например, 2999-02-01T22:22:00+03
//                     "2019-03-17T16:00:00+03"
                    mResult.setText(time);

                    if(token == null){
                        Toast.makeText(getApplicationContext(), R.string.tokenIsNull, Toast.LENGTH_SHORT).show();
                    }else if(nameStreamET.getText().toString().trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.nameStreamIsNull, Toast.LENGTH_SHORT).show();
                    }else {

                        allRequest.broadcastInsert(nameStreamET.getText().toString(), time, "public");
//                        allRequest.broadcastInsert("test", "2019-03-18T03:00:00+03", "public");
                        allRequest.streamInsert(nameStreamET.getText().toString()+time, "rtmp", "30fps", formatET.getText().toString());


                        while (true){
                            if(allRequest.getfinishInsertMethod() == 2){
                                allRequest.broadcastBind();
                                while (!allRequest.bindIsFinish()){}
                                break;
                            }
                        }
                        allRequest.setfinishInsertMethod(0);


                        Intent startStreamIntent = new Intent(StartActivity.this, VideoEncoderActivity.class);
//                        startStreamIntent.putExtra("idBroadcast", allRequest.getIdBroadcast());
//                        startStreamIntent.putExtra("YOUR_API_KEY", token);


                        startStreamIntent.putExtra("format", formatET.getText().toString());
                        startStreamIntent.putExtra("player1", player1ET.getText().toString());
                        startStreamIntent.putExtra("player2", player2ET.getText().toString());
//                        startStreamIntent.putExtra("rtmpUrl", allRequest.getAllParamsForRequest().getRtmpUrl());

                        startStreamIntent.putExtra("allRequestParamsForRequest", allRequest.getAllParamsForRequest());
                        startActivityForResult(startStreamIntent, RC_ENCOD_VIDEO);
                    }

                    break;
            }
        }
    }


}
