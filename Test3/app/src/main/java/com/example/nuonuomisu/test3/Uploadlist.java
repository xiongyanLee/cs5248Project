package com.example.nuonuomisu.test3;

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
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by shumin on 11/4/2017.
 */

public class Uploadlist extends AppCompatActivity {
    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private ListView mListView;
    private String _UserName = "test";
    final String path = "/storage/emulated/0/Android/data/com.example.nuonuomisu.test3/files/";
    private String _sessionKey = "uubyn2w8wqizmj0g7vfrhkawsdg4opmy";
    private HttpPostRequest getSessionHTTP;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadlist);
        mListView = (ListView) findViewById(R.id.upload_list);

        refreshUpdateList2(path);

//        getSessionHTTP = new HttpPostRequest("UTF-8");
//        try {
//            Log.d("HTTP", "Start to get session id ");
//
//            String httpResult = getSessionHTTP.execute("session", _UserName).get();
//            Log.d("HTTP", "Get session id: "+ httpResult);
//            _sessionKey = httpResult;
//
//        } catch (ExecutionException|InterruptedException e){
//            Log.d("HTTP", "Cannot get sesssion id");
//            e.printStackTrace();
//        }

        Log.d("HTTP", "Get session id: "+ _sessionKey);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                String  itemValue    = (String) mListView.getItemAtPosition(position);
                uploadPopup(itemValue, path);
            }
        });

    }
//
//    public void RecorderActivity(View view){
//        Intent intent  = new Intent(this, Recorder.class);
//        startActivity(intent);
//    }

    private void refreshUpdateList(String path){
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        Vector<String> rlist = new Vector<>();

        for (int i = 0; i < files.length; i++)
        {
            if (files[i].getName().contains("Recording")
                    &!files[i].getName().contains("_")
                    &files[i].getName().contains(".mp4")){
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




    private void refreshUpdateList2(String path){
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        Vector<String> rlist = new Vector<>();


        for (int i = 0; i < files.length; i++)
        {
            if (files[i].getName().contains("Recording")
                    &!files[i].getName().contains("_")
                    &files[i].getName().contains(".mp4")){
                rlist.add(files[i].getName());
            }
        }
        String[] values = new String[rlist.size()];
        String[] desc = new String[rlist.size()];

        for (int i = 0; i < rlist.size(); i++)
        {
            values[i] = rlist.get(i);
            desc[i] = readStatus(rlist.get(i));
        }

        CustomListAdapter adapter=new CustomListAdapter(this, values, desc);
        mListView.setAdapter(adapter);
    }

    private String readStatus(String file){
        String tempFile = file.substring(0, file.indexOf("."))+"temp.txt";

        String fileName = path + tempFile;
        String result = "Invalid";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            if((line = bufferedReader.readLine()) != null) {
                result = line;
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return result;
    }

    private String readPosition(String file){
        String tempFile = file.substring(0, file.indexOf("."))+"temp.txt";

        String fileName = path + tempFile;
        String result = "Invalid";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            if((line = bufferedReader.readLine()) != null) {
                result = line;
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return result;
    }

    private String readSid(String file){
        String tempFile = file.substring(0, file.indexOf("."))+"temp.txt";

        String fileName = path + tempFile;
        String result = "Invalid";

        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            line = bufferedReader.readLine();
            if((line = bufferedReader.readLine()) != null) {
                result = line;
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return result;
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

        popupView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File f = new File(filePath + fileName);
                f.delete();
                popupWindow.dismiss();
                refreshUpdateList2(filePath);
            }
        });

        popupView.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (cutAndUploadVideo(filePath, fileName)){
                    popupWindow.dismiss();
                    uploadSuccessPopup(filePath);
                } else {
                    popupWindow.dismiss();
                    uploadFailurePopup(filePath);
                }

            }
        });

    }

    public void uploadFailurePopup(final String filePath){
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.upload_list_layout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_uploadfailure, null);

        // create the popup window
        int width = 400;
        int height = 200;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        popupView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                refreshUpdateList2(filePath);
            }
        });
    }

    public void uploadSuccessPopup(final String filePath){
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.upload_list_layout);

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_uploadsuccess, null);

        // create the popup window
        int width = 400;
        int height = 200;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
        popupView.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                refreshUpdateList2(filePath);
            }
        });
    }

    private boolean cutAndUploadVideo(String path, String name){

        String _sid= readSid(name);


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this, Uri.fromFile(new File(path+name)));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        retriever.release();

        if (_sid.equals("-2")) {
            getSessionHTTP = new HttpPostRequest("UTF-8");
            try {
                Log.d("HTTP", "Start to get sid ");

                String httpResult = getSessionHTTP.execute("sid", _sessionKey, name, "Description").get();
                Log.d("HTTP", "Get sid: " + httpResult);
                _sid = httpResult;

            } catch (ExecutionException | InterruptedException e) {
                Log.d("HTTP", "Cannot get sid");
                e.printStackTrace();
            }
        }
        Log.d("HTTP", "Sid: "+_sid);


        //--------------------
        Log.d("Cut", "Full Video Length: " + Long.toString(timeInMillisec));
        Log.d("Cut", "Original File: " + path + name);

        String n = name.substring(0, name.indexOf("."));
        int count = 0;              // num of files

        if (timeInMillisec > 3000) {
            boolean cutSuccess = false;
            boolean uploadSuccess = false;

            /// Setup  ------------------------------------------
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
                Log.d("Cut", "FFmpeg is not supported by device");
            }

            long countTime = timeInMillisec;

            long start = 0;
            long end = 3000;
            int resumePos = 0;


            if (readStatus(name).equals("Failed")){
                resumePos = Integer.parseInt(readPosition(name));
                Log.d("Cut", "Resume postition: " + resumePos);
            }
            start = resumePos * 3000;
            end = resumePos * 3000 + 3000;

            while (countTime > 0) {
                String _fileURL = path + n + "_" + count + ".mp4";
                String _fileName = n + "_" + count + ".mp4";
                String _dur = "";

                /// Cut  ------------------------------------------
                Log.d("CUT", "=========Start to Cut file========");

                start = count * 3000;
                if (countTime > 3000) {
                    end = count * 3000 + 3000;
                    _dur = "3000";
                } else {
                    end = timeInMillisec;
                    _dur = Long.toString(timeInMillisec - start);
                }

                String startString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(start),
                        TimeUnit.MILLISECONDS.toMinutes(start) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(start)),
                        TimeUnit.MILLISECONDS.toSeconds(start) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(start)));
                String endString = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(end),
                        TimeUnit.MILLISECONDS.toMinutes(end) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(end)),
                        TimeUnit.MILLISECONDS.toSeconds(end) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(end)));

                Log.d("HTTP", "name: "+ _fileURL);
                Log.d("CUT", "Start Point: "+startString);
                Log.d("CUT", "End Point: "+endString);
                Log.d("CUT", "Duration:" + _dur);

                String[] cmd = new String[]{"-y", "-i", path + name, "-ss", startString, "-vcodec", "copy",
                        "-acodec", "copy", "-t", endString, "-strict", "-2", _fileURL};

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
                    cutSuccess = true;

                } catch ( Exception e) {
                    Log.d("CUT", "FFmpegCommandAlreadyRunningException");
                    e.printStackTrace();
                    cutSuccess = false;
                }


                /// Upload  ------------------------------------------
                if (!cutSuccess) {
                    recordDown(path+name, "Failed", Integer.toString(count), _sid);
                    Log.d("CUT", _fileName + " cutting failed");
                    return false;
                } else {
                    Log.d("CUT", _fileName + " is cut successfully");
                }

                Log.d("HTTP", "=========Start to Send HTTP request========");
                String httpResult;
                //Instantiate new instance of our class
                HttpPostRequest getRequest;

                try {
                    getRequest = new HttpPostRequest("UTF-8");

                    Log.d("HTTP", "fileURL: "+ _fileURL);
                    Log.d("HTTP", "name: "+ _fileName);
                    Log.d("HTTP", "session: "+ _UserName);
                    Log.d("HTTP", "duration: "+ _dur);

                    httpResult = getRequest.execute("clip", _fileURL, _fileName, _sessionKey, _sid, _dur).get();
                    Log.d("HTTP", "Final Result: "+ httpResult);


                    if (httpResult.equals("200")){
                        uploadSuccess = true;
                    } else {
                        uploadSuccess = false;
                    }

                } catch (ExecutionException|InterruptedException e){
                    Log.d("HTTP", "Error");
                    e.printStackTrace();
                    uploadSuccess = false;
                }

                // delete the file
                File f = new File(_fileURL);
                f.delete();
                Log.d("HTTP", _fileName + " is delete successfully");

                if (!uploadSuccess) {
                    recordDown(path+name, "Failed", Integer.toString(count), _sid);
                    Log.d("HTTP", _fileName + " uploading failed");
                    return false;
                } else {
                    recordDown(path+name, "Finished", "-2", _sid);
                    Log.d("HTTP", _fileName + " is uploaded successfully");
                }

                count++;
                countTime -= 3000;
            }

        } else {
            boolean uploadSuccess = false;

            // Rename the file
            File file = new File(path+name);
            File newFile = new File(path+n+"_0.mp4");
            file.renameTo(newFile);
            count = 0;

            String _filePath = path+n+"_0.mp4";
            String _fileName = n+"_0.mp4";
            String _dur = Long.toString(timeInMillisec);

            String httpResult;
            //Instantiate new instance of our class
            HttpPostRequest getRequest;

            try {
                getRequest = new HttpPostRequest("UTF-8");

                Log.d("HTTP", "path: "+ _filePath);
                Log.d("HTTP", "name: "+ _fileName);
                Log.d("HTTP", "session: "+ _UserName);
                Log.d("HTTP", "duration: "+ _dur);

                httpResult = getRequest.execute(_filePath, _fileName, _UserName, _dur).get();
                Log.d("HTTP", "Final Result: "+ httpResult);
                Log.d("HTTP", httpResult);

                if (httpResult.equals("200")){
                    Log.d("HTTP", "if");
                    uploadSuccess = true;
                } else {
                    Log.d("HTTP", "else");
                    uploadSuccess = false;
                }

            } catch (ExecutionException|InterruptedException e){
                Log.d("HTTP", "Error");
                e.printStackTrace();
                uploadSuccess = false;
            }
            // Rename back the file
            newFile.renameTo(file);

            if (!uploadSuccess) {
                recordDown(path+name, "Failed", Integer.toString(count), _sid);
                Log.d("HTTP", _fileName + " uploading failed");
                return false;
            } else {
                recordDown(path+name, "Finished", "-2", _sid);
                Log.d("HTTP", _fileName + " is uploaded successfully");
            }

            count = 1;
        }
        
//        boolean uploadingFail = false;
//
//        for (int i = 0; i< count; i++){
//
//            Log.d("HTTP", "Number: "+ i);
//
//            long dur = 0;
//            if (i==count-1){
//                dur = lastDuration;
//            }else {
//                dur = 3000;
//            }
//
//            //Some url endpoint that you may have
//
//            //String to place our result in
//            String result;
//            //Instantiate new instance of our class
//            HttpPostRequest getRequest;
//            //Perform the doInBackground method, passing in our url
//
//            String _filePath = path + n + "_" + i + ".mp4";
//            String _fileName = n + "_" + i + ".mp4";
//            String _session = "";
//            String _dur = ""+dur;
//
//            try {
//                getRequest = new HttpPostRequest("UTF-8");
//
//                Log.d("HTTP", "path: "+ _filePath);
//                Log.d("HTTP", "name: "+ _fileName);
//                Log.d("HTTP", "session: "+ _session);
//                Log.d("HTTP", "duration: "+ _dur);
//
//                result = getRequest.execute(_filePath, _fileName, _session, _dur).get();
//
//                if (result != "200"){
//                    uploadingFail = true;
//                    break;
//                }
//                Log.d("HTTP", "Final Reuslt: "+ result);
//            } catch (ExecutionException |InterruptedException|IOException e){
//                Log.d("HTTP", "Error");
//                e.printStackTrace();
//            }
//
//        }
        return true;

    }

    private void recordDown(String fullPath, String status, String stopPoint, String sid){
        Log.d("WRITE", "Full: "+fullPath);

        String location = fullPath.substring(0, fullPath.lastIndexOf("Recor"));
        String fileName = fullPath.substring(fullPath.lastIndexOf("Recor"));
        fileName = fileName.substring(0, fileName.indexOf("."));

        Log.d("WRITE", "Location: "+location);
        Log.d("WRITE", "Name: "+fileName);
        String txtfile = location+fileName+"temp.txt";
        Log.d("WRITE", "Text file: "+txtfile);
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(txtfile);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            bufferedWriter.write(status);
            bufferedWriter.newLine();
            bufferedWriter.write(stopPoint);
            bufferedWriter.newLine();
            bufferedWriter.write(sid);

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

}
