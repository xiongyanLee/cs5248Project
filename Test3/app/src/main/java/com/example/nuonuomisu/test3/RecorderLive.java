package com.example.nuonuomisu.test3;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by nuonuomisu on 4/11/17.
 */

public class RecorderLive extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder_live);

        getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment_live.newInstance())
                    .commit();
    }
}
