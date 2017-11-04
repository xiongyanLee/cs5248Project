package com.example.nuonuomisu.test3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;


public class CameraActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ViewFlipper flipview = (ViewFlipper) findViewById(R.id.myViewFlipper);
        setContentView(R.layout.activity_camera);
        //flipview.setDisplayedChild(flipview.indexOfChild(findViewById(R.id.record_page)));
        Button start_recording = findViewById(R.id.button_start);
        start_recording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
            }
        });
        Button stream = findViewById(R.id.stream_page);
        //Button record = findViewById(R.id.back_record);
        //record.setOnClickListener(new View.OnClickListener() {
         //   public void onClick(View v) {
                //setContentView(R.layout.activity_camera);
                //Toast.makeText(activity, "switch back", Toast.LENGTH_SHORT).show();
          //  }
       // });
        //playerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
    }

    public void playerlistActivity(View view){
        Intent intent  = new Intent(this, Playerlist.class);
        startActivity(intent);
    }




}
