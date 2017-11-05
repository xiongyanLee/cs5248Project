package com.example.nuonuomisu.test3;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

import static java.lang.Thread.sleep;

/**
 * Created by xiongyan on 11/4/2017.
 */

public class Uploadlist extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;
    int clickCounter=0;
    boolean wait;
    AsyncHttpClient uploadClient = new AsyncHttpClient();



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
            if (files[i].getName().contains("Recording")&!files[i].getName().contains("_")){
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


                cutVideo(filePath, fileName);
                popupWindow.dismiss();
                updateFile(filePath);
            }
        });


    }

    private void cutVideo(String path, String name){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, Uri.fromFile(new File(path+name)));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        retriever.release();

        Log.d("Cut", Long.toString(timeInMillisec));
        Log.d("Cut", path + name);

        String n = name.substring(0, name.indexOf("."));
        int count = 0;              // num of files
        Long lastDuration = timeInMillisec;

        if (timeInMillisec > 3000) {
            /// Load
            FFmpeg ffmpeg = FFmpeg.getInstance(this);

            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure() {
                    }

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFinish() {
                    }
                });
            } catch (FFmpegNotSupportedException e) {
                // Handle if FFmpeg is not supported by device
            }


            long countTime = timeInMillisec;

            long start = 0;
            long end = 3000;

            while (countTime > 0) {
                start = count * 3000;
                if (countTime > 3000) {
                    end = count * 3000 + 3000;
                } else {
                    end = timeInMillisec;
                    lastDuration = timeInMillisec - start;
                }
                String startString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(start),
                        TimeUnit.MILLISECONDS.toMinutes(start) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(start)),
                        TimeUnit.MILLISECONDS.toSeconds(start) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(start)));
                String endString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(end),
                        TimeUnit.MILLISECONDS.toMinutes(end) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(end)),
                        TimeUnit.MILLISECONDS.toSeconds(end) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end)));


                String[] cmd = new String[]{"-y", "-i", path + name, "-ss", startString, "-vcodec", "copy",
                        "-acodec", "copy", "-t", endString, "-strict", "-2", path + n +"_"+count+".mp4"};

                try {
                    ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            Log.i("VideoEditActivity", "Success " + message);
                            // is_video_generated_ = true;
                        }

                        @Override
                        public void onProgress(String message) {
                            Log.i("VideoEditActivity", "Progress updated " + message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e("VideoEditActivity", "ERROR! " + message);
                        }

                        @Override
                        public void onStart() {
//                            progress_dialog_.setMessage(getString(R.string.str_video_generating));
//                            progress_dialog_.show();
                        }

                        @Override
                        public void onFinish() {
                            Log.i("VideoEditActivity", "Finished");
//                            progress_dialog_.hide();
//
//                            Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
//                            intent.putExtra("media", edited_video_path_);
//                            startActivity(intent);
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }

                count++;
                countTime -= 3000;
            }

        } else {
            File file = new File(path+name);
            File newFile = new File(path+n+"_0.mp4");
            file.renameTo(newFile);
            lastDuration = timeInMillisec;
            count = 1;
        }
        Log.d("CUT", "Last Duration: "+lastDuration);
        Log.d("CUT", "File Count: "+count);


        Log.d("HTTP", "=========Start to Send HTTP request========");

        for (int i = 0; i< 1; i++){
            wait = true;

            Log.d("HTTP", "Number: "+ i);

            long dur = 0;
            if (i==count-1){
                dur = lastDuration;
            }else {
                dur = 3000;
            }

            File myFile = new File(path + n + "_" + count + ".mp4");
            RequestParams params = new RequestParams();

            Log.d("HTTP", "name: "+ n + "_" + count + ".mp4");
            Log.d("HTTP", "session: "+ "Test");
            Log.d("HTTP", "duration: "+ dur);


            try {
                params.put("file", myFile);
                params.put("name", n + "_" + count + ".mp4");
                params.put("session", "Test");
                params.put("duration", dur);

            } catch (FileNotFoundException e) {
            }



            Log.d("HTTP", "=========Before Post Request");
            uploadClient.post(this,
                    "http://172.25.104.202:8000/streaming/",
                    null,
                    params,
                    "multipart/form-data",
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                            // called when response HTTP status is "200 OK"
                            wait=false;
                            Log.d("HTTP", "=========Post request success");
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            wait=false;
                            Log.d("HTTP", "=========Post request fail");
                        }
                    });

            while(wait) {
                try {
                    sleep(100);
                    Log.d("HTTP", "------Waiting-----");
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

//
//        File f = new File(path + name);
//        f.delete();
    }


}
