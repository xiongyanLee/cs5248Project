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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        for (int i=0; i<listItems.size(); i++){
            Log.d("HTTP", "name is "+ listItems.get(i));

        }
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        //listItems.remove(0);
        //adapter.notifyDataSetChanged();
    }

    public void get_video_list(){
        String response = "";
        ArrayList<String> video_list = new ArrayList<String>();
        HttpPostRequest get_video = new HttpPostRequest("UTF-8");
            try {
                String httpResult = get_video.execute("index").get();
                response = httpResult;

            } catch (ExecutionException | InterruptedException e) {
                Log.d("HTTP", "Cannot get sid");
                e.printStackTrace();
            }

        try{
            JSONArray jarray = new JSONArray(response);

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jsonobject = jarray.getJSONObject(i);
                String id = jsonobject.getString("id");
                String name = jsonobject.getString("name");
                video_list.add(name);
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }

        listItems = video_list;
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
        String uri = "http://monterosa.d2.comp.nus.edu.sg:80/view/"+Integer.toString(position)+"/.mpd";
        Intent intent  = new Intent(this, Player.class);
        intent.putExtra("uri",uri);
        startActivity(intent);
    }

    public void cameraActivity(View view){
        Intent intent  = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

}
