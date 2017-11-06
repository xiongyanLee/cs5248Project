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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

            //Some url endpoint that you may have

            //String to place our result in
            String result;
            //Instantiate new instance of our class
            HttpGetRequest getRequest;
            //Perform the doInBackground method, passing in our url
            try {
                getRequest = new HttpGetRequest("UTF-8");

                result = getRequest.execute(path + n + "_" + i + ".mp4", n + "_" + i + ".mp4", "Test", ""+dur).get();

                Log.d("HTTP", "Final Reuslt "+ result);
            } catch (ExecutionException|InterruptedException|IOException e){
                Log.d("HTTP", "ERRRRRRRRR");
                e.printStackTrace();
            }


            Log.d("HTTP", "name: "+path + n + "_" + i + ".mp4");
            Log.d("HTTP", "session: "+ "Test");
            Log.d("HTTP", "duration: "+ dur);


        }


    }

    public byte[] read(File file) throws IOException {

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        }finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    public int uploadFile(String sourceFileUri) {

        int serverResponseCode = 0;

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        Log.d("HTTP", "In the upload");
        if (!sourceFile.isFile()) {

            //dialog.dismiss();
            Log.d("HTTP", "file not exist");

//            runOnUiThread(new Runnable() {
//                public void run() {
//                    Log.d("HTTP", "Source File not exist :");
//                }
//            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://119.28.108.175:5000/streaming/");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);
                Log.d("http", "before GET OUT put stream");
                dos = new DataOutputStream(conn.getOutputStream());
                Log.d("http", "after GET OUT put stream");
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                                + fileName + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                Log.d("http", "before create buffer");

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                Log.d("http", "after bytes read");

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                Log.d("http", "after writng files");

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                Log.d("http", "before get response code");
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.d("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    ;

                            Log.d("http", msg);

                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                //dialog.dismiss();
                ex.printStackTrace();

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Log.d("HTTP", "MalformedURLException Exception : check script url.");
//                    }
//                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();
                Log.d("HTTP", "Exception : "  + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Log.d("HTTP", "Got Exception : see logcat ");
//
//                    }
//                });

            }
            //dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

}
