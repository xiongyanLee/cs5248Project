package com.example.nuonuomisu.test3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class CameraActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final ViewFlipper flipview = (ViewFlipper) findViewById(R.id.myViewFlipper);
        setContentView(R.layout.activity_camera);

        //Button start_recording = findViewById(R.id.button_start);
        //Button stream = findViewById(R.id.stream_page);

    }

    public void playerlistActivity(View view){
        Intent intent  = new Intent(this, Playerlist.class);
        startActivity(intent);
    }

    public void recorderActivity(View view){
        Intent intent  = new Intent(this, Recorder.class);
        startActivity(intent);
    }

    public void recorderLiveActivity(View view){
        Intent intent  = new Intent(this, RecorderLive.class);
        startActivity(intent);
    }


}
