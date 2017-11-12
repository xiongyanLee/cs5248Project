package com.example.nuonuomisu.test3;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by xiongyan on 11/4/2017.
 */

public class Playerlist extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;
    HashMap<String,String> videoName=new HashMap<String,String>();
    int clickCounter=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerlist);
        mListView = (ListView) findViewById(R.id.video_list);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        mListView.setClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Log.i("HelloListView", "You clicked Item: " + id + " at position:" + position);
                Log.i("HelloListView", "You clicked video name is: " + listItems.get(position));
                //Object o = mListView.getItemAtPosition(position);
                playerActivity(position);
            }
        });
        updatePlayerList(null);
    }

    public void updatePlayerList(View view) {
        get_video_list();
        listItems.add("my name is 2");
        listItems.add("my name is 3");
        //listItems.remove(0);
        adapter.notifyDataSetChanged();
    }

    public void get_video_list(){
        String response = "";
        ArrayList<String> video_list = new ArrayList<String>();
        HttpPostRequest get_video = new HttpPostRequest("UTF-8");
            try {
                Log.d("HTTP", "Start to get sid ");

                String httpResult = get_video.execute("index").get();
                Log.d("HTTP", "Get index: " + httpResult);
                response = httpResult;

            } catch (ExecutionException | InterruptedException e) {
                Log.d("HTTP", "Cannot get sid");
                e.printStackTrace();
            }
        listItems.add("myname is 1");
    }

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.video_list);
        }
        return mListView;
    }
    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    public void playerActivity(int position){
        String uri="";
        HttpPostRequest get_mpd = new HttpPostRequest("UTF-8");
        try {
            Log.d("HTTP", "Start to get mpd ");

            String httpResult = get_mpd.execute("mpd", "sid", listItems.get(position)).get();
            Log.d("HTTP", "Get mpd: " + httpResult);
            uri = httpResult;

        } catch (ExecutionException | InterruptedException e) {
            Log.d("HTTP", "Cannot get sid");
            e.printStackTrace();
        }
        Intent intent  = new Intent(this, Player.class);
        intent.putExtra("uri",uri);
        startActivity(intent);
    }

    public void cameraActivity(View view){
        Intent intent  = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}
