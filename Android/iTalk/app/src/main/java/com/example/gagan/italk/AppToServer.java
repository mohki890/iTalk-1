package com.example.gagan.italk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/******
 * Need work on Cookies,,, Sometimes,, not retrived correctly
 */


/**
 * Created by gagan on 21/5/15.
 */
public class AppToServer {
    private static String data_uname="";
    private static String data_nname="";
    private static int data_id=-1;

    private static List data_all_id=null;
    private static List data_all_uname=null;
    private static List data_all_nname=null;
    private static List OnlineID=null;





    private static String mainAdd="";
    private static RequestQueue queue;
    private static String cookie="";

    private static void updateMember(Activity activity)
    {
        if(queue==null)
            queue= Volley.newRequestQueue(activity);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(activity);
        mainAdd=preferences.getString("server_address","");
        cookie=preferences.getString("cookie","");



    }
    private static void saveCookie(Activity activity,String _cookie)
    {
        if(_cookie==null) _cookie="";
        cookie=_cookie;
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("cookie",_cookie);
        editor.commit();
        //Toast.makeText(activity,"Saving Cookie\n"+_cookie, Toast.LENGTH_SHORT).show();

    }




    public static void tryLogin(Activity activity,String username,String password, final Callable<Void> onSuccess)
    {
        updateMember(activity);
        String url=mainAdd+"/login.php";

        /***For Post***/
        HashMap<String,String> map=new HashMap<String,String>();
        map.put("user",username);
        map.put("pwd",password);

        //Toast.makeText(activity,"Cookie used :"+cookie,Toast.LENGTH_SHORT).show();
        final Activity activity1=activity;
        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null) {
                    Toast.makeText(activity1,"Error in Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AppToServer.saveCookie(activity1,(String)arrayList.get(2));
                if(cookie.equals(""))
                {
                    Toast.makeText(activity1,"Error in Connection! Try Again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(activity1,"Request Completed!\n"+arrayList.toString(),Toast.LENGTH_SHORT).show();

                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    try {
                        onSuccess.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    String msg = "";
                    switch (sc)
                    {
                        case 204 :
                            msg="Invalid Details";
                            break;
                        default:
                            msg="Unexpected Error!";
                    }
                    Toast.makeText(activity1, msg, Toast.LENGTH_SHORT).show();
                }

            }


        };
        fC.execute(url,map,"POST",cookie);

    }
    public static void testLoggedIn(Activity activity, final Callable<Void> onSuccess)
    {
        updateMember(activity);
        String url=mainAdd+"/checkIfLoggedin.php";

        //Toast.makeText(activity,"Request Starting Cookie="+cookie,Toast.LENGTH_SHORT).show();


        final Activity activity1=activity;
        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null) {
                    return;
                }
                //AppToServer.saveCookie(activity1,(String)arrayList.get(2));
                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    try {
                        onSuccess.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        };
        fC.execute(url,null,"POST",cookie);

    }
    public static void doLogout(final Activity activity)
    {
        updateMember(activity);
        String url=mainAdd+"/logout.php";
        //Toast.makeText(activity,"Cookie used :"+cookie,Toast.LENGTH_SHORT).show();

        final Activity activity1=activity;
        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null) {
                    Toast.makeText(activity1,"Error in Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AppToServer.saveCookie(activity1,(String)arrayList.get(2));

                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    Intent in=new Intent(activity1,MainActivity.class);
                    activity1.startActivity(in);
                    activity1.finish();

                }
                else Toast.makeText(activity1, "Can't Logout", Toast.LENGTH_SHORT).show();

            }


        };
        fC.execute(url,null,"POST",cookie);

    }


    public static void setMyInfo(final Activity activity, final Callable<Void> callme)
    {

        fetchContent fC=new fetchContent(){
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null) return;
                if((int)arrayList.get(0)!=200) return;

                String out=(String)arrayList.get(1);

                try {
                    JSONObject jo=new JSONObject(out);

                    data_uname=jo.getString("uname");
                    data_nname=jo.getString("nname");
                    data_id=jo.getInt("id");
                    if(callme!=null) try {
                        callme.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        fC.execute(mainAdd+"/myInfo.php",null,"POST",cookie);


    }

    public static void setAllUserInfo(final Activity activity, final Callable<Void> callme,final Callable<Void> finalCallMe)
    {

        fetchContent fC=new fetchContent(){
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                try {
                    if (arrayList == null) return;
                    if ((int) arrayList.get(0) != 200) return;

                    String out = (String) arrayList.get(1);
                    data_all_id = new ArrayList();
                    data_all_uname = new ArrayList();
                    data_all_nname = new ArrayList();

                    try {
                        JSONArray ja = new JSONArray(out);
                        for (int i = 0; ; i++) {
                            if (ja.isNull(i)) break;
                            JSONObject jo = ja.getJSONObject(i);

                            String uname = jo.getString("uname");
                            String nname = jo.getString("nname");
                            int id = jo.getInt("id");

                            data_all_id.add(id);
                            data_all_uname.add(uname);
                            data_all_nname.add(nname);

                        }
                        if (callme != null) try {
                            callme.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                finally {
                    if(finalCallMe!=null)
                        try {
                            finalCallMe.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        };
        fC.execute(mainAdd+"/allUser.php",null,"POST");


    }


    public static void sendMessage(final Activity activity,String msg,int other_id, final Callable<Void> onSuccess)
    {

        HashMap<String,String> map=new HashMap<String,String>();
        map.put("id",""+other_id);
        map.put("msg",msg);

        fetchContent fC=new fetchContent(){
            @Override
            protected void onPostExecute(ArrayList arrayList) {

                if(arrayList==null || (int)arrayList.get(0)!=200){
                    return ;
                }
                if(onSuccess!=null)
                    try {
                        onSuccess.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


            }
        };
        fC.execute(mainAdd+"/sendMsg.php",map,"POST",cookie);


    }

    public static void readMessage(final Activity activity, int other_id,int last_message_min,int last_message_max, final boolean isNewMessage)
    {

        HashMap<String,String> map=new HashMap<String,String>();
        map.put("id",""+other_id);
        map.put("last_msgid_min",""+last_message_min);
        map.put("last_msgid_max",""+last_message_max);


        fetchContent fC=new fetchContent(){
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null || (int)arrayList.get(0)!=200){
                    return ;
                }
                String out=(String)arrayList.get(1);


                try {
                    JSONArray ja = new JSONArray(out);
                    ArrayList t_id=new ArrayList<Integer>();
                    ArrayList t_filetype=new ArrayList<Integer>();
                    ArrayList t_type=new ArrayList<Integer>();
                    ArrayList t_text=new ArrayList<String>();
                    ArrayList t_time=new ArrayList<String>();

                    for (int i = 0; ; i++) {
                        if (ja.isNull(i)) break;
                        JSONObject jo = ja.getJSONObject(i);

                        String text = jo.getString("text");
                        String time = jo.getString("time");
                        int id = jo.getInt("id");
                        int type = jo.getInt("type");
                        int filetype = jo.getInt("filetype");

                        t_id.add(id);
                        t_type.add(type);
                        t_text.add(text);
                        t_time.add(time);
                        t_filetype.add(filetype);
                    }

                    ((chatRoom)activity).setMsgReceived(t_id, t_type, t_text, t_time,t_filetype, isNewMessage);


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        };
        fC.execute(mainAdd+"/getMsg.php",map,"POST",cookie);


    }

    public static void readLatestMessage(final Activity activity, final Callable<Void> onSuccess,final Callable<Void> onFinally, final ArrayList<Integer> t_id, final ArrayList t_filetype, final ArrayList<Integer> t_type, final ArrayList<String> t_text, final ArrayList<String> t_time, final ArrayList<Integer> t_other  )
    {

        HashMap<String,String> map=new HashMap<String,String>();
        map.put("id","-1");


        fetchContent fC=new fetchContent(){
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                try {
                    if (arrayList == null || (int) arrayList.get(0) != 200) {
                        return;
                    }
                    String out = (String) arrayList.get(1);


                    try {
                        JSONArray ja = new JSONArray(out);
                        t_id.clear();
                        t_type.clear();
                        t_text.clear();
                        t_time.clear();
                        t_filetype.clear();
                        t_other.clear();
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = ja.getJSONObject(i);

                            String text = jo.getString("text");
                            String time = jo.getString("time");
                            int id = jo.getInt("id");
                            int type = jo.getInt("type");
                            int filetype = jo.getInt("filetype");
                            int other = jo.getInt("other");

                            t_id.add(id);
                            t_type.add(type);
                            t_text.add(text);
                            t_time.add(time);
                            t_filetype.add(filetype);
                            t_other.add(other);

                        }
                        if (onSuccess != null) {
                            onSuccess.call();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }finally {
                    if(onFinally!=null) {
                        try {
                            onFinally.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        fC.execute(mainAdd+"/getMsg.php",map,"POST",cookie);


    }



    public static void uploadFileDPBase64(Activity activity,String SbitmapImg,String SbitmapThumb, final Callable<Void> onSuccess)
    {   if(SbitmapImg==null || SbitmapThumb==null) return;




        String url=mainAdd+"/data/dp/upload.php";

        /***For Post***/
        HashMap<String,String> map=new HashMap<String,String>();
        map.put("image",SbitmapImg);//5MB
        map.put("thumb",SbitmapThumb);//50KB

        //Toast.makeText(activity,"Cookie used :"+cookie,Toast.LENGTH_SHORT).show();
        final Activity activity1=activity;
        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null) {
                    Toast.makeText(activity1,"Error in Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String msg;
                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    msg="Done!";
                    if(onSuccess!=null)
                    {
                        try {
                            onSuccess.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    msg="Unexpected Error!";
                }
                Toast.makeText(activity1,msg,Toast.LENGTH_SHORT).show();

            }




        };
        fC.execute(url,map,"POST",cookie);

    }
    public static void downloadFileImage(Activity activity,String url, final ImageView imageView)
    {
        downloadFileImage(activity,url,imageView,null,null);
    }
    static Bitmap temp_bitmap;
    public static void downloadFileImage(Activity activity,String url, final ImageView imageView,final Callable<Void> onFailure,final Callable<Void> onSuccess)
    {


        /***For Post***/
        HashMap<String,String> map=new HashMap<String,String>();


        final Activity activity1=activity;
        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if (arrayList == null) {
                    try {
                        onFailure.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return ;
                }
                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    String out=(String)arrayList.get(1);
                    new BitmapFromBase64(){
                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            if(bitmap!=null && imageView!=null) {
                                imageView.setImageBitmap(bitmap);
                                try {
                                    temp_bitmap=bitmap;
                                    onSuccess.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else try {
                                onFailure.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(out);

                }
                else try {
                    onFailure.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        fC.execute(url,null,"POST","");
        return ;
    }
    public static void changeMyDetails(final Activity activity, final String nname, final String oldpassword,final String password,final Callable<Void> onSuccess)
    {

        HashMap<String,String> map=new HashMap<String,String>();
        if(nname!=null)map.put("nname",nname);
        if(password!=null)
        {
            if(oldpassword==null) return;
            map.put("oldpwd",oldpassword);
            map.put("pwd",password);

        }

        fetchContent fC=new fetchContent(){
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null || (int)arrayList.get(0)!=200){
                    if(nname!=null)
                        Toast.makeText(activity,"Couldn't Change Your Nickname now!!",Toast.LENGTH_SHORT).show();
                    if(password!=null)
                        Toast.makeText(activity,"Couldn't Change Your Password now!!",Toast.LENGTH_SHORT).show();
                    return ;
                }
                if((int)arrayList.get(0)==200)
                {
                    data_nname=nname;
                    if(onSuccess!=null)
                        try {
                            onSuccess.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }


            }
        };
        fC.execute(mainAdd+"/setDetails.php",map,"POST",cookie);


    }

    /***************************
     *
     * @param activity
     * @param fileCont
     * @param fileType
     *  0   -   NoFile
     *  1   -   Downloadable
     *  2   -   Png
     *  3   -   Jpg
     * @param onSuccess
     * @param onFailure
     */
    public static void uploadFileDBBase64(Activity activity,int other_id,String fileName,String fileCont,int fileType, final Callable<Void> onSuccess, final Callable<Void> onFailure)
    {   if(fileCont==null) {
        if(onFailure!=null)
            try {
                onFailure.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return;
    }




        String url=mainAdd+"/uploadFile.php";

        /***For Post***/
        HashMap<String,String> map=new HashMap<String,String>();
        map.put("id",""+other_id);
        map.put("file",fileCont);
        map.put("filetype",""+fileType);
        map.put("text",""+fileName);
        final Activity activity1=activity;
        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList==null) {
                    Toast.makeText(activity1,"Error in Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String msg;
                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    msg="File Uploaded";
                    if(onSuccess!=null)
                    {
                        try {
                            if(onSuccess!=null)
                            onSuccess.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        try {
                            if(onFailure!=null)
                                onFailure.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                else {
                    msg="Unexpected Error! "+sc;
                    try {
                        if(onFailure!=null)
                        onFailure.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(activity1,msg,Toast.LENGTH_SHORT).show();

            }




        };
        fC.execute(url,map,"POST",cookie);

    }


    public static void downloadFileDB(final Activity activity,final int other_id,final int file_id, final File file,final Callable<Void> onFailure,final Callable<Void> onSuccess)
    {

        Toast.makeText(activity,"Downloading File",Toast.LENGTH_SHORT).show();
        String url=mainAdd+"/downloadFile.php";

        /***For Post***/
        HashMap<String,String> map=new HashMap<String,String>();
        map.put("id",""+other_id);
        map.put("fileid",""+file_id);

        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if (arrayList == null) {
                    try {
                        onFailure.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return ;
                }
                int sc=(int)arrayList.get(0);
                if(sc==200)
                {
                    String out=(String)arrayList.get(1);
                    new FileFromBase64(1024*1024*10,activity){
                        @Override
                        protected void onPostExecute(Boolean isDone) {
                            super.onPostExecute(isDone);
                            if(isDone)
                                try {
                                    onSuccess.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            else try {
                                onFailure.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.execute(out,file.getAbsolutePath());

                }
                else{
                    try {
                        onFailure.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        };
        fC.execute(url,map,"POST",cookie);
        return ;
    }
    public static void justPingServer()
    {
        justPingServer(false,null);
    }
    public static void justPingServer(boolean makeNull,final Callable<Void> onSuccess)
    {

        String url=mainAdd+"/pingMe.php";
        if(makeNull)
        {
            url=mainAdd+"/pingMe.php?makeNull=1";
        }

        fetchContent fC=new fetchContent() {
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList!=null)
                    if(((int)arrayList.get(0))==200)
                    {
                        if(onSuccess!=null)
                            try {
                                onSuccess.call();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
            }
        };
        fC.execute(url,null,"GET",cookie);

    }
    public static void refreshOnline(final Callable<Void> onSuccess)
    {

        String url=mainAdd+"/online.php";

        fetchContent fC=new fetchContent() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList!=null)
                    if(((int)arrayList.get(0))==200)
                    {
                        if(OnlineID==null)
                            OnlineID=new ArrayList<Integer>();
                        try {
                            JSONArray array=new JSONArray((String)arrayList.get(1));
                            OnlineID.clear();
                            for(int i=0;i<array.length();i++)
                                OnlineID.add(array.getInt(i));
                            if(onSuccess!=null)
                                try {
                                    onSuccess.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
            }
        };
        fC.execute(url,null,"GET",cookie);

    }
    public static void testUserOnline(final chatRoom activity,final int id,final Callable<Void> onSuccess)
    {

        String url=mainAdd+"/online.php?id="+id;

        fetchContent fC=new fetchContent() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            protected void onPostExecute(ArrayList arrayList) {
                if(arrayList!=null)
                try {
                    if(((int)arrayList.get(0))==200)
                    {
                        JSONArray ar= null;
                            ar = new JSONArray( (String) arrayList.get(1));
                        if(ar.length()>=2)
                        {
                            activity.serverTime= ar.getString(0);
                            activity.lasttime= ar.getString(1);
                        }

                    }
                    else if(((int)arrayList.get(0))==201)
                    {
                        activity.lasttime= "";
                    }
                    if(onSuccess!=null)
                    try {
                        onSuccess.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    } catch (JSONException e2) {
                    e2.printStackTrace();
                }

            }
        };
        fC.execute(url,null,"GET",cookie);

    }





    public static String getUserName()
    {
        return data_uname;
    }
    public static String getNickName()
    {
        return data_nname;
    }
    public static int getId()
    {
        return data_id;
    }
    public static List<Integer> getAllUserId()
    {
        return data_all_id;
    }
    public static List<String> getAllUserUname()
    {
        return data_all_uname;
    }
    public static List<String> getAllUserNname()
    {
        return data_all_nname;
    }
    public static List<Integer> getOnlineIDS()
    {
        return OnlineID;
    }


    public static String getMainAdd(){return mainAdd;}




}

class AdditionalFunctions
{
    public static String getDP_URL(int id,boolean isThumb)
    {
        String url=AppToServer.getMainAdd()+"/data/dp/"+id;
        if(isThumb)url=AppToServer.getMainAdd()+"/data/dp/t_"+id;
        return  url;
    }
    public static void uploadFileDP(Activity activity,Bitmap bitmapImg, final Bitmap bitmapThumb, final Callable<Void> onSuccess) {
        if (bitmapImg == null || bitmapThumb == null) return;

        new Base64FromBitmap(1024 * 1024 * 5, activity, "FileSize Should be less 5MB") {
            @Override
            protected void onPostExecute(final String base64Image) {
                super.onPostExecute(base64Image);
                if (base64Image == null) return;
                //For Thumbnail

                new Base64FromBitmap(1024 * 500, activity, "FileSize Should be less 500KB") {
                    @Override
                    protected void onPostExecute(String base64Thumb) {
                        super.onPostExecute(base64Thumb);
                        if (base64Thumb == null) return;
                        AppToServer.uploadFileDPBase64(activity, base64Image, base64Thumb, onSuccess);
                    }
                }.execute(bitmapThumb);




            }
        }.execute(bitmapImg);
    }

    public static void uploadFileDB(final Activity activity,final int other_id, final File file, final Callable<Void> onSuccess, final Callable<Void> onFailure) {
        if (file == null ) return;
        Toast.makeText(activity,"Preparing Upload",Toast.LENGTH_SHORT).show();
        new Base64FromFile(1024 * 1024 * 10, activity, "FileSize Should be less 10MB") {

            @Override
            protected void onPostExecute(final String base64File) {
                super.onPostExecute(base64File);
                if (base64File == null) return;
                //For Thumbnail

                AppToServer.uploadFileDBBase64(activity,other_id,file.getName() ,base64File,1, onSuccess, onFailure);




            }
        }.execute(file);
    }

    public static void uploadFileDBstream(final Activity activity,final int other_id,final String fname, final InputStream is, final Callable<Void> onSuccess, final Callable<Void> onFailure) {
        Toast.makeText(activity,"Preparing Upload",Toast.LENGTH_SHORT).show();
        new Base64FromFile(1024 * 1024 * 10, activity, "FileSize Should be less 10MB") {

            @Override
            protected void onPostExecute(final String base64File) {
                super.onPostExecute(base64File);
                if (base64File == null) return;
                //For Thumbnail

                AppToServer.uploadFileDBBase64(activity,other_id,fname ,base64File,1, onSuccess, onFailure);




            }
        }.execute();
    }

}
class Base64FromBitmap extends AsyncTask<Bitmap,Void,String>
{
    int size;
    Activity activity;
    String msg;
    Base64FromBitmap(int size,Activity activity,String msg)
    {
        this.size=size;
        this.activity=activity;
        this.msg=msg;
    }
    @Override
    protected String doInBackground(Bitmap... params) {
        if(params.length<1) return null;
        Bitmap bitmap=(Bitmap)params[0];
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
        String str=Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

        if(str.length()>=size)
        {
            return null;
        }

        return str;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s==null)
        {
            Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        }
    }
}

class Base64FromFile extends AsyncTask<File,Void,String>
{
    int size;
    boolean isLimitOver=false;
    Activity activity;
    String msg;
    InputStream is=null;

    Base64FromFile(int size,Activity activity,String msg)
    {
        this.size=size;
        this.activity=activity;
        this.msg=msg;
    }
    Base64FromFile(int size,Activity activity,String msg,InputStream is)
    {
        this.size=size;
        this.activity=activity;
        this.msg=msg;
        this.is=is;
    }

    @Override
    protected String doInBackground(File... params) {
        byte[] bytes=null;
        if(is==null) {
            if (params.length < 1) return null;
            File file = (File) params[0];
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            bytes = new byte[(int) file.length()];
            try {
                fileInputStream.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            try {
                byte[] b=new byte[size];
                int len=is.read(b);
                bytes = new byte[len];
                for(int i=0;i<len;i++)bytes[i]=b[i];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(bytes==null)
            return null;
        String str=Base64.encodeToString(bytes,Base64.DEFAULT);

        if(str.length()>=size)
        {
            isLimitOver=true;
            return null;
        }

        return str;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(s==null)
        {
         if(isLimitOver)
             Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
        else
             Toast.makeText(activity,"Can't Upload!",Toast.LENGTH_SHORT).show();
        }
    }
}
class FileFromBase64 extends AsyncTask<String,Void,Boolean>
{
    static String LOG_TEXT=FileFromBase64.class.getSimpleName();
    int size;
    Activity activity;
    FileFromBase64(int size,Activity activity)
    {

        this.size=size;
        this.activity=activity;
    }
    @Override
    protected Boolean doInBackground(String... params) {

        if(params.length<2) return false;
        String str=(String)params[0];

        File file=new File(params[1]);


        FileOutputStream fileOutputStream=null;

        try {
            fileOutputStream=new FileOutputStream(file);

            fileOutputStream.write( Base64.decode(str,Base64.DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if(fileOutputStream!=null)
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean done) {
        super.onPostExecute(done);
        if(done==false)
        {
        }
    }
}

class BitmapFromBase64 extends AsyncTask<String,Void,Bitmap>
{

    @Override
    protected Bitmap doInBackground(String... params) {
        if(params.length<1) return null;
        String str=(String)params[0];

        ByteArrayInputStream byteArrayInputStream =new ByteArrayInputStream(Base64.decode(str,Base64.DEFAULT));
        Bitmap bitmap=BitmapFactory.decodeStream(byteArrayInputStream);

        return bitmap;


    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

    }
}



/*
 For Volley -- Not Working
 public static void tryLogin(Activity activity,String username,String password)
    {
        updateMember(activity);
        String url=mainAdd+"/login.php";



HashMap<String,String> map=new HashMap<String,String>();
map.put(SendPost_RC.COOKIE,cookie);

        Uri u=Uri.parse(url).buildUpon()
        .appendQueryParameter("user",username)
        .appendQueryParameter("pwd",password).build();

final Activity activity1=activity;
        SendPost_RC request= new SendPost_RC( u.toString(), map, new Response.Listener<JSONObject>() {
@Override
public void onResponse(JSONObject response) {
        if(response.has("cookie"))
        {
        try {
        AppToServer.saveCookie(activity1,response.getString("cookie"));
        } catch (JSONException e) {
        e.printStackTrace();
        }
        }
        if(response.has("sc"))
        try {
        int sc=(int)response.get("sc");
        if(sc==200)
        {
        Intent in=new Intent(activity1,UserRoom.class);
        activity1.startActivity(in);
        activity1.finish();

        }
        else {
        String msg = "";
        switch (sc)
        {
        case 204 :
        msg="Invalid Details";
        break;
default:
        msg="Unexpected Error!";
        }
        Toast.makeText(activity1, msg, Toast.LENGTH_SHORT).show();
        }
        } catch (JSONException e) {
        e.printStackTrace();
        }
        }
        }, new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        Toast.makeText(activity1,"Error in Connection!", Toast.LENGTH_SHORT).show();
        }
        }) ;

        queue.add(request);

        }
public static void doLogout(final Activity activity)
        {
        updateMember(activity);
        String url=mainAdd+"/logout.php";

        HashMap<String,String> map=new HashMap<String,String>();
        map.put(SendPost_RC.COOKIE,cookie);

final Activity activity1=activity;
        SendPost_RC request= new SendPost_RC( url, map, new Response.Listener<JSONObject>() {
@Override
public void onResponse(JSONObject response) {
        if(response.has("cookie"))
        {
        try {
        AppToServer.saveCookie(activity1,response.getString("cookie"));
        } catch (JSONException e) {
        e.printStackTrace();
        }
        }
        if(response.has("sc"))
        try {
        int sc=(int)response.get("sc");
        if(sc==200)
        {
        Intent in=new Intent(activity1,MainActivity.class);
        activity1.startActivity(in);
        activity1.finish();

        }
        else Toast.makeText(activity1, "Can't Logout", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
        e.printStackTrace();
        }
        }
        }, new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        Toast.makeText(activity1,"Error in Connection!", Toast.LENGTH_SHORT).show();
        }
        }) ;

        queue.add(request);
        }


public static void getMeJson(Activity activity,String add,Response.Listener<JSONObject> listener)
        {

        String url=mainAdd+"/"+add;

        HashMap<String,String> map=new HashMap<String,String>();
        map.put(SendPost_RC.COOKIE,cookie);
        Toast.makeText(activity,"Cookie : "+cookie,Toast.LENGTH_SHORT).show();


final Activity activity1=activity;
        SendPost_RC request= new SendPost_RC(true, url, map,listener, new Response.ErrorListener() {
@Override
public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        Toast.makeText(activity1,"Error : "+error.toString(),Toast.LENGTH_SHORT).show();

        }
        }) ;

        queue.add(request);


        }
*/
