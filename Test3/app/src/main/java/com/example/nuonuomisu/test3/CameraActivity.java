package com.example.nuonuomisu.test3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Button start_recording = findViewById(R.id.button_start);
        start_recording.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();

            }
        });
        Button stream = findViewById(R.id.stream_page);
        stream.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.live_stream);
            }
        });


        //Button record = findViewById(R.id.back_record);
        //record.setOnClickListener(new View.OnClickListener() {
         //   public void onClick(View v) {
                //setContentView(R.layout.activity_camera);
                //Toast.makeText(activity, "switch back", Toast.LENGTH_SHORT).show();
          //  }
       // });
    }
}
