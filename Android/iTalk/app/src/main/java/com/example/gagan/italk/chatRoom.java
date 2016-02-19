package com.example.gagan.italk;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.service.media.MediaBrowserService;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gagan.italk.SmallActivity.ImageActivity;
import com.example.gagan.italk.com.example.gagan.italk.SmallFunctions.TimeConvertor;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;


public class chatRoom extends ActionBarActivity {
    final int REFRESH_TIMER=1000;
    final static int FILETYPE_IMAGE=2;
    final static int FILETYPE_VIDEO=3;
    final static int FILETYPE_DATA=4;


    private  File dir=null;

    private ProgressBar ac_progressbar;
    private ImageView iv_isUploading;
    private String other_uname,other_nname;
    private EditText et_msg;
    private  ImageButton b_send;
    private TextView tv_loadMore;
    private boolean isLoadMoreVisible=false;
    private int other_id,pos_inList,last_msg_id_min=-1,last_msg_id_max=-1;
    ChatArrayAdapter chatAdapter;
    private ListView lv_chatcon;
    private continousRefresh continousRefresh_obj;
    private ImageView tv_otherDP;
    private Drawable other_DP;

    String lasttime="",serverTime="";


    private TextView tv_ab_un,tv_ab_nn;
    private ImageButton ib_ab_dp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
            tv_ab_un= (TextView) findViewById(R.id.cr_action_bar_un);
            tv_ab_nn= (TextView) findViewById(R.id.cr_action_bar_nn);
            ib_ab_dp= (ImageButton) findViewById(R.id.cr_action_bar_dp);
            ib_ab_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageActivity.startMe(chatRoom.this, AdditionalFunctions.getDP_URL(other_id, false),other_nname);
                }
            });
        findViewById(R.id.cr_action_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatRoom.this.finish();
            }
        });
        findViewById(R.id.cr_action_bar_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAttachements();
            }
        });
        findViewById(R.id.cr_action_bar_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatRoom.this.openOptionsMenu();
            }
        });

         ac_progressbar= (ProgressBar) findViewById(R.id.cr_action_bar_process);

        tv_otherDP=new ImageView(this);//(ImageView)findViewById(R.id.id_chatroom_tv_otherimg);
        tv_otherDP.setImageResource(UserRoom.resource_noDP);
        dir=getAlbumStorageDir("iTalk");
        Intent myIntent=getIntent();

        boolean loaded_correctly=false;
        if(myIntent.hasExtra("other_id") && myIntent.hasExtra("other_uname") && myIntent.hasExtra("other_nname"))
        {
            other_id=myIntent.getIntExtra("other_id",-1);
            other_uname=myIntent.getStringExtra("other_uname");
            other_nname=myIntent.getStringExtra("other_nname");
            if(myIntent.hasExtra("pos"))
                pos_inList=myIntent.getIntExtra("pos",-1);
            else pos_inList=-1;
            if(!(other_id==-1 || other_nname==null || (other_uname!=null && other_uname.equals("")) || other_uname==null))
                loaded_correctly=true;

        }

        if(!loaded_correctly) { finish();return;}
        tv_loadMore=(TextView)findViewById(R.id.id_chatroom_tv_loadmore);

        lv_chatcon=(ListView)findViewById(R.id.id_chatroom_lv_chat);
        chatAdapter=new ChatArrayAdapter(this,R.layout.layout_chatroom_chat_send);
        lv_chatcon.setAdapter(chatAdapter);
        lv_chatcon.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0 && (last_msg_id_min > 1 || last_msg_id_min == -1))
                {
                    if(!isLoadMoreVisible)tv_loadMore.setVisibility(View.VISIBLE);
                    isLoadMoreVisible=true;

                }
                    else if(isLoadMoreVisible){
                    tv_loadMore.setVisibility(View.GONE);
                    isLoadMoreVisible=false;

                }

            }
        });
        lv_chatcon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final ChatMessage chatMessage = ((ChatMessage) chatAdapter.getItem(position));
                if (chatMessage.filetype == 1) {
                    if (dir == null) dir = getAlbumStorageDir("iTalk");
                    final File file = new File(dir, AppToServer.getId() + "_" + other_id + "_" +chatMessage.id+"_"+ chatMessage.msg);
                    if (file.exists()) {
                        launchFile(file);
                        return;
                    }
                    chatMessage.isDownloading = true;
                    chatMessage.isDownloaded = true;
                    chatAdapter.setDownloadingIcon(chatMessage, view);

                    AppToServer.downloadFileDB(chatRoom.this, other_id, chatMessage.id, file, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Toast.makeText(chatRoom.this, "File Not Downloaded!", Toast.LENGTH_SHORT).show();
                            chatMessage.isDownloaded = false;
                            chatMessage.isDownloading = false;
                            chatAdapter.setDownloadingIcon(chatMessage, view);
                            return null;
                        }
                    }, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            Toast.makeText(chatRoom.this, "File Saved\n" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            chatMessage.isDownloaded = true;
                            chatMessage.isDownloading = false;
                            chatAdapter.setDownloadingIcon(chatMessage, view);
                            return null;
                        }
                    });
                }
            }
        });
        iv_isUploading=(ImageView)findViewById(R.id.id_chatroom_iv_isUploading);
        tv_loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ac_progressbar.setVisibility(View.VISIBLE);
                    readMessages();
            }
        });
        chatAdapter.notifyDataSetChanged();

        et_msg=(EditText)findViewById(R.id.id_chatroom_et_msg);
        b_send=(ImageButton)findViewById(R.id.id_chatroom_b_send);
        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=et_msg.getText().toString();
                if(!text.isEmpty())
                {
                    sendMsg(text);
                    et_msg.setText("");
                }
            }
        });
        initOther(null);
        if(UserRoom.arrayAdapter!=null)
        {
           final int pos=UserRoom.arrayAdapter.getPositionById(other_id);
            if(pos>=0)
            {
                Drawable dp=UserRoom.arrayAdapter.getItem(pos).getDp();
                if(dp!=null)
                {
                    tv_otherDP.setImageDrawable(dp);
                    initOther(dp);

                }
                else
                {
                    AppToServer.downloadFileImage(this, AdditionalFunctions.getDP_URL(other_id, true), tv_otherDP,new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            other_DP=tv_otherDP.getDrawable();
                            initOther(other_DP);
                            UserRoom.arrayAdapter.getItem(pos).setBitmapDp(other_DP);
                            UserRoom.arrayAdapter.getItem(pos).getImageView_dp().setImageDrawable(other_DP);
                            return null;
                        }
                    },null);

                }
            }
        }
        readMessages();
        continousRefresh_obj=new continousRefresh();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_showDP) {
            ImageActivity.startMe(this, AdditionalFunctions.getDP_URL(other_id, false),other_nname);
            return true;
        } else if (id==R.id.action_attachment)
        {
            showAttachements();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        continousRefresh_obj.isRunning=false;
    }
    void showAttachements()
    {
        LayoutInflater inflater=getLayoutInflater();
        View v=View.inflate(this,R.layout.chat_specail_options,null);
        final AlertDialog dialog=new AlertDialog.Builder(this).create();
        dialog.setView(v);
        dialog.setTitle("Send File");
         (v.findViewById(R.id.ib_cso_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                captureMedia(1);
            }
        });
        v.findViewById(R.id.ib_cso_cvideo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                captureMedia(0);
            }
        });
        v.findViewById(R.id.ib_cso_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFileFromExplorer("image/*");
            }
        });
        v.findViewById(R.id.ib_cso_gvideo).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
            getFileFromExplorer("video/*");
            }
    });
        v.findViewById(R.id.ib_cso_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFileFromExplorer("file/*");
            }
        });




        dialog.show();

    }
    void initOther(Drawable dp)
    {
        tv_ab_nn.setText(other_nname);
        String OMsg="";
        if(!serverTime.isEmpty()) TimeConvertor.setTodayTimeStamp(serverTime);
        if(!lasttime.isEmpty())
            OMsg="("+TimeConvertor.getShortTime(lasttime)+") ";//serverTime;
        tv_ab_un.setText(other_uname + " " + OMsg);
        if(dp!=null)
            ib_ab_dp.setImageDrawable(dp);
        else
            ib_ab_dp.setImageResource(R.drawable.no_dp);


    }
    void justChangeActionSubtitle()
    {
        if(!serverTime.isEmpty()) TimeConvertor.setTodayTimeStamp(serverTime);
        if(!lasttime.isEmpty())
            tv_ab_un.setText(other_uname + " (" + TimeConvertor.getShortTime(lasttime) + ")");
        else
            tv_ab_un.setText(other_uname);


    }


    void sendMsg(String msg)
    {
        AppToServer.sendMessage(this,msg,other_id,new Callable<Void>() {
            @Override
            public Void call() throws Exception {
             //Messages Are Automatically Refreshed
             //   readMessagesJustNew();
                return null;
            }
        });
    }
    void readMessages()
    {
        if(last_msg_id_max<0)
            AppToServer.readMessage(this,other_id,-1,-1,true);
        else
            AppToServer.readMessage(this,other_id,last_msg_id_max+1,-1,true);
        if(last_msg_id_min>0)
        {
            AppToServer.readMessage(this,other_id,Math.max(0,last_msg_id_min-10),last_msg_id_min-1,false);
        }

    }
    void readMessagesJustNew()
    {
        if(last_msg_id_max>0)
            AppToServer.readMessage(this,other_id,last_msg_id_max+1,-1,true);
        else
            AppToServer.readMessage(this,other_id,-1,-1,true);

    }

    void setMsgReceived(ArrayList<Integer> temp_id,ArrayList<Integer> temp_type,ArrayList<String> temp_text,ArrayList<String> temp_time,ArrayList<Integer> temp_filetype,boolean isNewMessage)
    {

        if(isNewMessage)
        {
            for(int i=0;i<temp_id.size();i++)
            {
                addToAdapter(chatAdapter,temp_id.get(i),temp_type.get(i),temp_text.get(i),temp_time.get(i),temp_filetype.get(i));
            }
            if(temp_id.size()>0)
            {
                chatAdapter.notifyDataSetChanged();
            }
            ac_progressbar.setVisibility(View.GONE);



        }
        else
        {

            ChatArrayAdapter aa=chatAdapter=new ChatArrayAdapter(this,R.layout.layout_chatroom_chat_send);
            for(int i=0;i<temp_id.size();i++)
            {
                addToAdapter(aa,temp_id.get(i),temp_type.get(i),temp_text.get(i),temp_time.get(i),temp_filetype.get(i));
            }
            aa.addAll(chatAdapter);
            chatAdapter=aa;
            lv_chatcon.setAdapter(chatAdapter);
            lv_chatcon.smoothScrollToPosition(temp_id.size());
            if(temp_id.size()>0)
            {
                //CHECK IT
                //Move List View To start of List
            }


        }
        if(temp_id.size()>0)
        {

            if(chatAdapter.getCount()>0) {
                last_msg_id_min =((ChatMessage) chatAdapter.getItem(0)).id;
                last_msg_id_max =((ChatMessage) chatAdapter.getItem(chatAdapter.getCount()-1)).id;

            }

        }
    }
    void addToAdapter(ArrayAdapter aa,int id,int type,String text,String time,int filetype)
    {
        boolean isSend=true;
        if(type%2!=0)
            isSend=false;
        boolean downloaded=false;
        if(filetype!=0) {
            final File file = new File(dir, AppToServer.getId() + "_" + other_id + "_" + id + "_"+text);

            if (file.exists())
            {
                downloaded=true;

            }
        }

        ChatMessage message=new ChatMessage(isSend,text,time,id,filetype,downloaded,false);
            aa.add(message);

    }


    class continousRefresh implements Runnable
    {
        Thread t;
        boolean isRunning;
        continousRefresh()
        {
            t=new Thread(this);
            isRunning=true;
            t.start();
        }

        @Override
        public void run() {
            while (isRunning)
            {
                readMessagesJustNew();
                AppToServer.testUserOnline(chatRoom.this,chatRoom.this.other_id,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                    justChangeActionSubtitle();
                        return null;
                    }
                });
                try {
                    t.sleep(REFRESH_TIMER);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }




    File temp_file=null;

    /***
     *
     * @param mediaType
     * 0    -   Video
     * 1    -   image
     * 2    -   Audio
     */
    void captureMedia(int mediaType)
    {
        temp_file=null;

        Intent takePictureIntent = null;
        switch (mediaType)
        {
            case 0: takePictureIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0.3);
                break;
            case 1: takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                break;
            case 2: takePictureIntent=new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);

                break;
            default:return;
        }
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            switch (mediaType)
            {
                case 0: temp_file=UserRoom.getTempImageFile("Video","mp4");
                    break;
                case 1:  temp_file=UserRoom.getTempImageFile("Image","jpg");
                    break;
                case 2:  temp_file=UserRoom.getTempImageFile("Audio","wav");
                    break;
                default:return;
            }
          if(temp_file==null)
            {
                Toast.makeText(this, "Error in Creating Temp. File!", Toast.LENGTH_SHORT).show();
                return;
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp_file));
            startActivityForResult(takePictureIntent, 1);
        } else
            Toast.makeText(this, "No App Camera Found!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                iv_isUploading.setVisibility(View.VISIBLE);
                AdditionalFunctions.uploadFileDB(this,other_id, temp_file, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (temp_file.exists()) temp_file.delete();
                        iv_isUploading.setVisibility(View.GONE);
                        return null;
                    }
                }, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if(temp_file.exists())temp_file.delete();
                        iv_isUploading.setVisibility(View.GONE);
                        return null;
                    }
                });

            }
            else
            if(resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this,"Cancelled!",Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 2) {

                if (resultCode == RESULT_OK) {
                    try
                    {

                    Uri path=data.getData();
                    InputStream is=getContentResolver().openInputStream(path);
                    String fname="file";
                        try {
                            Cursor returnCursor = getContentResolver().query(path, null, null, null, null);

                            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            if (nameIndex >= 0)
                                fname = returnCursor.getString(nameIndex);
                        }catch (Exception e)
                        {
                            Toast.makeText(this,"Not Done!2"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    iv_isUploading.setVisibility(View.VISIBLE);


                        AdditionalFunctions.uploadFileDBstream(this,other_id,fname, is,new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            iv_isUploading.setVisibility(View.GONE);

                            return null;
                        }
                    },new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            iv_isUploading.setVisibility(View.GONE);

                            return null;
                        }
                    });
                    }catch (Exception e)
                    {
                        Toast.makeText(this,"Not Done!"+e.toString(),Toast.LENGTH_SHORT).show();

                    }
                }
                else
                if(resultCode == RESULT_CANCELED)
                {
                    Toast.makeText(this,"Cancelled!",Toast.LENGTH_SHORT).show();
                }
            }

    }

    void getFileFromExplorer(String tag)
    {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType(tag);

        startActivityForResult(Intent.createChooser(intent,"Pick File"), 2);
    }
    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.exists() && !file.mkdirs()) {
           Toast.makeText(this,"Directory not created : "+file.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    void launchFile(File file)
    {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String ext=getExtension(file);
        String type=null;
        switch (ext)
        {
            case "apk" : type="application/vnd.android.package-archive";break;
            case "txt" :
            case "csv" :
            case "xml" :
            case "htm" :
            case "html" :
            case "php" : type="text/*";break;

            case "png" :
            case "gif" :
            case "jpg" :
            case "jpeg" :
            case "bmp" : type="image/*";break;

            case "mp3" :
            case "wav" :
            case "ogg" :
            case "mid" :
            case "amr" :
            case "midi" : type="audio/*";break;

            case "mp4" :
            case "mpeg":
            case "avi" :
            case "3gp" : type="video/*";break;

            default:  type="*/*";

        }
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            startActivity(intent);
        }catch (Exception e)
        {
        try
        {
            intent.setDataAndType(Uri.fromFile(file), "*/*");
            startActivity(intent);
        }
        catch (Exception e2)
        {
            Toast.makeText(this,"File is Already Saved At\n"+file.getAbsoluteFile(),Toast.LENGTH_SHORT).show();
        }
        }

    }
    String getExtension(File file)
    {
        String fname=file.getName();
        int i=fname.lastIndexOf(".");
        if(i>=0 && i+1<fname.length())
        {
            return fname.substring(i+1);
        }
        return null;
    }
}
class ChatMessage
{
    int id,filetype;
    boolean isSend,isDownloaded=false,isDownloading=false;
    String msg;
    String time;
    ChatMessage(boolean _isSend,String _msg,String _time,int _id,int _filetype,boolean _isDownloaded,boolean _isDownloading)
    {
        isSend=_isSend;
        msg=_msg;
        time=_time;
        id=_id;
        filetype=_filetype;
        isDownloaded=_isDownloaded;
        isDownloading=_isDownloading;
    }
}
class ChatArrayAdapter extends ArrayAdapter
{
    private List<ChatMessage> list;
    private List<Integer> list_id;
    private Context context;
    public ChatArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context=context;
        list=new ArrayList<ChatMessage>();
        list_id=new ArrayList<>();
    }

    @Override
    public void add(Object object) {
        super.add(object);
        if(list_id.indexOf(((ChatMessage) object).id)<0) {
            list.add((ChatMessage) object);
            list_id.add(((ChatMessage)object).id);
        }
    }

    @Override
    public void addAll(Collection collection) {
        super.addAll(collection);
        Iterator iterator=collection.iterator();
        while (iterator.hasNext())
        {
            ChatMessage chatMessage=(ChatMessage)iterator.next();
            if(list_id.indexOf(chatMessage.id)<0)
            {
                list.add(chatMessage);
                list_id.add(chatMessage.id);
            }

        }

    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage=list.get(position);
        View viewMsg=null;
        LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (chatMessage.filetype) {
            case 0:
                if (chatMessage.isSend)
                    viewMsg = inflater.inflate(R.layout.layout_chatroom_chat_send, null);
                else
                    viewMsg = inflater.inflate(R.layout.layout_chatroom_chat_receive, null);
                break;
            case 1:
                if (chatMessage.isSend)
                    viewMsg = inflater.inflate(R.layout.layout_chatroom_chat_send_file, null);
                else
                    viewMsg = inflater.inflate(R.layout.layout_chatroom_chat_receive_file, null);
                setDownloadingIcon(chatMessage,viewMsg);

                break;
        }
        ((TextView)(viewMsg.findViewById(R.id.id_chatroom_tv_chat_ele))).setText(chatMessage.msg);
        ((TextView)(viewMsg.findViewById(R.id.id_chatroom_tv_chat_time))).setText(TimeConvertor.getJustShortTime(chatMessage.time));


        return viewMsg;
    }
    void setDownloadingIcon(ChatMessage chatMessage,View viewMsg)
    {
        int isDown=R.id.id_chatroom_iv_chat_download,isDown_ing=R.id.id_chatroom_iv_chat_downloading;
        if(chatMessage.isDownloaded)
            ((ImageView)(viewMsg.findViewById(isDown))).setVisibility(View.GONE);
        else
            ((ImageView)(viewMsg.findViewById(isDown))).setVisibility(View.VISIBLE);
        if(chatMessage.isDownloading)
            ((ImageView)(viewMsg.findViewById(isDown_ing))).setVisibility(View.VISIBLE);
        else
            ((ImageView)(viewMsg.findViewById(isDown_ing))).setVisibility(View.GONE);

    }
}