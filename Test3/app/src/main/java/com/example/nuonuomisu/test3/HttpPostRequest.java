package com.example.nuonuomisu.test3;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by nuonuomisu on 6/11/17.
 */

public class HttpPostRequest extends AsyncTask<String, Void, String> {

    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    private static final String LINE_FEED = "\r\n";
    private String charset;
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";

    public HttpPostRequest(String charset) {
        this.charset = charset;

        // creates a unique boundary based on time stamp
    }

    @Override
    protected String doInBackground(String... params){

        switch (params[0]){
            case "clip":
                return postVideoClip(params[1], params[2], params[3], params[4], params[5]);
            case "session":
                return postGetSession(params[1]);
            case "sid":
                return postGetSid(params[1], params[2], params[3]);
            case "index":
                return getIndex();
            default:
                log.d("HTTP", "Wrong http command");
                return "Wrong http command";
        }
    }

    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }

    private String postVideoClip(String sourceFileUri,
                                 String name,
                                 String session,
                                 String sid,
                                 String duration){

        int serverResponseCode = 0;

//        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 4 * 1024 * 1024;

        File sourceFile = new File(sourceFileUri);


        if (!sourceFile.isFile()) {
            Log.d("HTTP", "file not exist");
            return "0";
        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://monterosa.d2.comp.nus.edu.sg:32770/streaming/");

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(2000);

                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary+";charset=UTF-8");
//                conn.setRequestProperty("file", sourceFileUri);
//                conn.setRequestProperty("name", name);
//                conn.setRequestProperty("sid", sid);
//                conn.setRequestProperty("session", session);
//                conn.setRequestProperty("duration", duration);
//                conn.setRequestProperty("description", "Desc");
//
                Log.d("HTTP", "--" + sourceFileUri);
                Log.d("HTTP", "--" + name);
                Log.d("HTTP", "--" + sid);
                Log.d("HTTP", "--" + session);
                Log.d("HTTP", "--" + duration);

                Log.d("http", "Open output stream");

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + sourceFileUri + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                Log.d("http", "Create a Byte Buffer");

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                Log.d("http", "Start to read file and write");

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                Log.d("http", "after bytes read");

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                Log.d("http", "Finish writing files");

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"name\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(name + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"session\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(session + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"sid\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(sid + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"duration\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(duration + lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("desc" + lineEnd);

                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                Log.d("http", "Get response code");
                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.d("HTTP", "HTTP Response is : "
                        + serverResponseMessage + "    Code: " + serverResponseCode);

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

//        return "0";
    }

    private String postGetSession(String userName){

        String session = "Invalid Session";
        String inputLine = "";

        HttpURLConnection conn = null;

        try {

            // open a URL connection to the Servlet
            URL url = new URL("http://monterosa.d2.comp.nus.edu.sg:32770/login/");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", userName);

            Log.i("HTTP", "JSON: "+jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    inputLine += line;
                }
            } else {
                inputLine = "";
            }
            Log.d("HTTP" , "msg:" + inputLine);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Map<String, String> map2 = gson.fromJson(inputLine, new TypeToken<Map<String, String>>() {}.getType());
        Log.d("HTTP", map2.get("session"));

        return map2.get("session");

    }

    private String postGetSid(String session, String fileName, String description){

        String sid = "Invalid sid";
        String inputLine = "";

        HttpURLConnection conn = null;

        try {

            // open a URL connection to the Servlet
            URL url = new URL("http://monterosa.d2.comp.nus.edu.sg:32770/stream/");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept","application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("session", session);
            jsonParam.put("name", fileName);
            jsonParam.put("description", description);

            Log.i("HTTP", "json: "+jsonParam.toString());
            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
            os.writeBytes(jsonParam.toString());

            os.flush();
            os.close();

            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    inputLine += line;
                }
            } else {
                inputLine = "";
            }
            log.d("HTTP", "CODE: "+conn.getResponseCode());
            Log.d("HTTP" , "msg:" + inputLine);

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("HTTP", inputLine);


        return inputLine;
    }

    private String getIndex(){
        HttpURLConnection conn = null;
        String inputLine = "";
        try {

            // open a URL connection to the Servlet
            URL url = new URL("http://monterosa.d2.comp.nus.edu.sg:32770/stream");

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    inputLine += line;
                }
            } else {
                inputLine = "";
            }
            log.d("HTTP", "CODE: "+conn.getResponseCode());


            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputLine;

    }

}
