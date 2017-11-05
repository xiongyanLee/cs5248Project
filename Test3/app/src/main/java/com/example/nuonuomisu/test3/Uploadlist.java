package com.example.nuonuomisu.test3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by xiongyan on 11/4/2017.
 */

public class Uploadlist extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;
    int clickCounter=0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadlist);
        mListView = (ListView) findViewById(R.id.upload_list);


        final String path = "/storage/emulated/0/Android/data/com.example.nuonuomisu.test3/files/";

        updateFile(path);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                String  itemValue    = (String) mListView.getItemAtPosition(position);
                uploadPopup(itemValue, path);
            }
        });

    }

    public void RecorderActivity(View view){
        Intent intent  = new Intent(this, Recorder.class);
        startActivity(intent);
    }

    private void updateFile(String path){
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        Vector<String> rlist = new Vector<>();
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].getName().contains("Recording")){
                rlist.add(files[i].getName());
            }

        }
        String[] values = new String[rlist.size()];
        for (int i = 0; i < rlist.size(); i++)
        {
            values[i] = rlist.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        mListView.setAdapter(adapter);
    }

    public void uploadPopup(final String fileName, final String filePath) {

        // get a reference to the already created main layout
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.upload_list_layout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_askforname, null);

        // create the popup window
        int width = 400;
        int height = 200;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);

        popupView.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File f = new File(filePath + fileName);
                f.delete();
                popupWindow.dismiss();
                updateFile(filePath);
            }
        });

        popupView.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


    }


}
