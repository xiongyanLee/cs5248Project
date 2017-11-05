package com.example.nuonuomisu.test3;

import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by xiongyan on 11/4/2017.
 */

public class Playerlist extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;
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
                Object o = mListView.getItemAtPosition(position);
                playerActivity();
            }
        });
        updatePlayerList(null);
    }

    public void updatePlayerList(View view) {
        List video_list = get_video_list();
        listItems.add("Video "+clickCounter++);
        listItems.add("Video "+clickCounter++);
        //listItems.remove(0);
        adapter.notifyDataSetChanged();
    }

    public List get_video_list(){
        String response = "";
        List<String> video_list = new ArrayList<String>();
        try{
            URL url = new URL("http://");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches (false);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("ID", "25");
            jsonParam.put("description", "Real");
            jsonParam.put("enable", "true");
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        video_list.add(response);
        return video_list;
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

    public void playerActivity(){
        Intent intent  = new Intent(this, Player.class);
        String uri = "http://yt-dash-mse-test.commondatastorage.googleapis.com/media/motion-20120802-manifest.mpd";
        intent.putExtra("uri",uri);
        startActivity(intent);
    }

    public void cameraActivity(View view){
        Intent intent  = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}
