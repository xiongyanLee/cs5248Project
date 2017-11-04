package com.example.nuonuomisu.test3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

/**
 * Created by xiongyan on 11/4/2017.
 */

public class Playerlist extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerlist);
    }

    public void playerActivity(View view){
        Intent intent  = new Intent(this, Player.class);
        startActivity(intent);
    }

}
