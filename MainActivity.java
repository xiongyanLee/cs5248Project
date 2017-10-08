package nus.myapplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private final int VIDEO_REQUEST_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void captureVideo(View view){
        Intent camera_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilepath();
        Uri video_uri = Uri.fromFile(video_file);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(camera_intent,VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode==VIDEO_REQUEST_CODE){
           if (resultCode==RESULT_OK){
               Toast.makeText(getApplicationContext(),"Video successfully recorded", Toast.LENGTH_LONG).show();
           }
           else {
               Toast.makeText(getApplicationContext(),"Video capture failed", Toast.LENGTH_LONG).show();
           }
       }
    }

    public File getFilepath(){
        File folder = new File(getFilesDir(),"cs5248");
        if (!folder.exists()){
            folder.mkdir();
        }
        File video_file = new File(folder,"sample_video.mp4");
        File install_folder = getFilesDir();
        try{
        File text_file = new File(folder, "test.txt");
        FileWriter writer = new FileWriter(text_file);
        writer.append("First string is here to be written.");
        writer.flush();
        writer.close();}
        catch (IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return video_file;
    }

}