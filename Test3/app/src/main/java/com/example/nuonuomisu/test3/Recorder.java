package com.example.nuonuomisu.test3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by nuonuomisu on 4/11/17.
 */

public class Recorder extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder);

        getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
    }

    public void uploadListActivity(View view){
        Intent intent  = new Intent(this, Uploadlist.class);
        startActivity(intent);
    }
}
