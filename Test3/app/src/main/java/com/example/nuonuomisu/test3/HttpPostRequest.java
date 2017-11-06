package com.example.nuonuomisu.test3;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by nuonuomisu on 6/11/17.
 */

public class HttpPostRequest extends AsyncTask<String, Void, String> {

    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    private static final String LINE_FEED = "\r\n";
    private String charset;


    public HttpPostRequest(String charset)
            throws IOException {
        this.charset = charset;

        // creates a unique boundary based on time stamp

    }

    @Override
    protected String doInBackground(String... params){
        String sourceFileUri = params[0];
        String name = params[1];
        String session = params[2];
        String duration = params[3];

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
            Log.d("HTTP", "file not exist");
            return "0";

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
                conn.setRequestProperty("file", fileName);
                conn.setRequestProperty("name", name);
                conn.setRequestProperty("session", session);
                conn.setRequestProperty("duration", duration);

                Log.d("http", "before GET OUT put stream");
                dos = new DataOutputStream(conn.getOutputStream());
                Log.d("http", "after GET OUT put stream");
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
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
                    Log.d("HTTP", "Code"+ serverResponseCode);
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();
                Log.e("HTTP", "MalformedURLException: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();
                Log.d("HTTP", "Exception : "  + e.getMessage());

            }
            return ""+serverResponseCode;

        }
    }
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

}
