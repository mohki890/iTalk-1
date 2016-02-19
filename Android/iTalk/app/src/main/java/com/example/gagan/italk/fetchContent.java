package com.example.gagan.italk;

import android.os.AsyncTask;

import org.apache.http.HttpConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by gagan on 22/5/15.
 */
public class fetchContent extends AsyncTask<Object,Void,ArrayList> {

    public boolean done=false;
    private String getQuery(Map<String,String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        if(params==null) return "";
        Set set_parm=params.entrySet();
        Iterator<Map.Entry<String,String>> it= set_parm.iterator();
        while(it.hasNext()) {
            Map.Entry<String,String> m= it.next();

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(m.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(m.getValue(), "UTF-8"));

        }


        return result.toString();
    }



    @Override
    protected ArrayList doInBackground(Object... params) {
    if(params.length==0) return null;
    String _url=(String)params[0];
    HashMap<String,String> map;
    if(params.length<2) map=new HashMap<String,String>();
    else map=(HashMap)params[1];
    String method="GET";
    if(params.length>=3)
            method=(String)params[2];
        String cookie="";
    if(params.length>=4)
            cookie=(String)params[3];

        BufferedReader reader = null;
        String forecastJsonString = null;
        HttpURLConnection urlConnection=null;

        try {
            URL url=new URL(_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.addRequestProperty("Cookie", cookie);

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            OutputStream os=urlConnection.getOutputStream();
            BufferedWriter wr=new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            wr.write(getQuery(map));         //For post parameter
            wr.flush();
            wr.close();
            os.close();

            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null)//nothing to do
            {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }



            String new_cookie = urlConnection.getHeaderField("Set-Cookie");
            ArrayList<Object> ar=new ArrayList<>();
            ar.add(urlConnection.getResponseCode());
            ar.add(buffer.toString());
            ar.add(new_cookie);
            return ar;

        } catch (IOException e) {

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                }
            }

        }



    }


}
