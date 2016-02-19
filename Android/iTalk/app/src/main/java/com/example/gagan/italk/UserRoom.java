package com.example.gagan.italk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.example.gagan.italk.SmallActivity.ImageActivity;
import com.example.gagan.italk.com.example.gagan.italk.SmallFunctions.TimeConvertor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public class UserRoom extends ActionBarActivity implements Comparator<userinfo_element>, AdapterView.OnItemClickListener {

    static CustomAdapterUserRoomLV arrayAdapter=null;
    final static int resource_noDP=R.drawable.no_dp;
    ListView lv_list_user;
    boolean isRunningForeground;
    static Boolean isDPsLoaded=false;
    private TextView tv_nickname;
    OnlineTestThread onlineTestThread;
    //Last Message
    ArrayList lm_id=new ArrayList<Integer>();
    ArrayList lm_filetype=new ArrayList<Integer>();
    ArrayList lm_type=new ArrayList<Integer>();
    ArrayList lm_text=new ArrayList<String>();
    ArrayList lm_time=new ArrayList<String>();
    ArrayList lm_other=new ArrayList<Integer>();


    //private ImageView tv_myDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_room);
        isRunningForeground=true;

        final Activity activity = this;

        /*tv_myDP = (ImageView) findViewById(R.id.id_userroom_tv_myDP);
        tv_myDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageActivity.startMe(activity,AdditionalFunctions.getDP_URL(AppToServer.getId(),false),AppToServer.getNickName());
            }
        });*/
        tv_nickname= (TextView) findViewById(R.id.ur_action_bar_nickname);
        findViewById(R.id.ur_action_bar_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRoom.this.openOptionsMenu();
            }
        });
        AppToServer.setAllUserInfo(this, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                setAllDetails();
                return null;
            }
        }, new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                AppToServer.setMyInfo(activity, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        setDetails();

                        return null;
                    }
                });

                return null;
            }
        });

        lv_list_user = (ListView) findViewById(R.id.id_userhome_list);
        arrayAdapter = new CustomAdapterUserRoomLV(this);
        lv_list_user.setAdapter(arrayAdapter);

        lv_list_user.setOnItemClickListener(this);

            onlineTestThread=new OnlineTestThread(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onlineTestThread.STOP_TESTING=true;
    }

    public void refreshLastMessages()
    {
        AppToServer.readLatestMessage(this,new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for(int i=0;i<lm_id.size();i++)
                {
                    int pos=arrayAdapter.getPositionById((int)lm_other.get(i));

                    if(pos>=0)
                    {
                        userinfo_element item=arrayAdapter.getItem(pos);
                        item.setLastMessage((String)lm_text.get(i),((int)lm_type.get(i))%2==0?false:true,(int)lm_filetype.get(i),(String) lm_time.get(i));
                    }
                }
                arrayAdapter.sort(UserRoom.this);

                return null;
            }
        },new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if(!isDPsLoaded)UserRoom.this.startLoadingDP();
                return null;
            }
        },lm_id,lm_filetype,lm_type,lm_text,lm_time,lm_other);



    }
    @Override
    public int compare(userinfo_element lhs, userinfo_element rhs) {
        //Compaition For Time String
        return rhs.getLastMsgTime().compareTo(lhs.getLastMsgTime());

    }
    void refreshAdapterOnline()
    {
        List<Integer> OnlineID=AppToServer.getOnlineIDS();
        if(OnlineID==null) return;

        for(int i=0;i<arrayAdapter.getCount();i++)
        {
                userinfo_element item=arrayAdapter.getItem(i);
                item.setIsOnline(false);
        }
        for(int i=0;i<OnlineID.size();i++)
        {
            int pos=arrayAdapter.getPositionById((int)OnlineID.get(i));

            if(pos>=0)
            {
                userinfo_element item=arrayAdapter.getItem(pos);
                item.setIsOnline(true);
            }
        }
        arrayAdapter.notifyDataSetChanged();


    }



    public void search_list(View v)
    {
        /*Intent in=new Intent(this,UserSearch.class);
        startActivityForResult(in,0);
        */

        FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        UserSearch frag=new UserSearch();
        frag.setUserRoom(this);
        transaction.add(frag,"search");
        transaction.commit();

    }
    public void onSearchResult(int id)
    {
        int position=arrayAdapter.getPositionById(id);
        int index = AppToServer.getAllUserId().indexOf(id);
        if (index < 0) return;
        String o_nname = AppToServer.getAllUserNname().get(index);
        String o_uname = (String) AppToServer.getAllUserUname().get(index);
        startChat(this, id, o_uname, o_nname,position);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int o_id = (int) arrayAdapter.getItemId(position);
        int index = AppToServer.getAllUserId().indexOf(o_id);
        if (index < 0) return;
        String o_nname = AppToServer.getAllUserNname().get(index);
        String o_uname = (String) AppToServer.getAllUserUname().get(index);
        startChat(this, o_id, o_uname, o_nname,position);
    }

    public void startChat(Activity activity, int other_id, String uname, String nname,int pos) {

        Intent in = new Intent(this, chatRoom.class);
        in.putExtra("other_id", other_id);
        in.putExtra("pos", pos);
        in.putExtra("other_uname", uname);
        in.putExtra("other_nname", nname);


        startActivity(in);
    }

    public void setDetails() {
        setOfflineData();
        //AppToServer.downloadFileImage(this, AdditionalFunctions.getDP_URL(AppToServer.getId(), true), tv_myDP);

    }
    public void setOfflineData()
    {
       tv_nickname.setText(AppToServer.getNickName() + " (" + AppToServer.getUserName() + ")");
    }

    public void setAllDetails() {
        arrayAdapter.clear();
        List<Integer> ids = AppToServer.getAllUserId();
        List<String> unames = AppToServer.getAllUserUname();
        List<String> nnames = AppToServer.getAllUserNname();
        boolean flag=false;
        for(int i=0;i<ids.size();i++) {
            //ids.get(i) + " > " + unames.get(i) + " (" + nnames.get(i) + ")\n";
            arrayAdapter.add(new userinfo_element(unames.get(i), nnames.get(i), ids.get(i), (flag = !flag), null, "", -1, true));

        }

        arrayAdapter.notifyDataSetChanged();

        }
    void startLoadingDP(){
        List<Integer> orderOfIDS=new ArrayList<Integer>();
        for(int i=0;i<arrayAdapter.getCount();i++)
            orderOfIDS.add(arrayAdapter.getItem(i).getId());
        loadNextThumbDpRec(orderOfIDS,0,0);
    }
    public void loadNextThumbDpRec(final List<Integer> orderOfIDS, final int loadIndex,final int try_num)
    {
        if(loadIndex<orderOfIDS.size())
        {
            downloadDP(orderOfIDS.get(loadIndex),new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    if(try_num<1)
                        loadNextThumbDpRec(orderOfIDS,loadIndex,try_num+1);
                    else
                        loadNextThumbDpRec(orderOfIDS,loadIndex+1,0);

                    return null;
                }
            },new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    loadNextThumbDpRec(orderOfIDS,loadIndex+1,0);
                    return null;
                }
            });
        }
        else
        {
            isDPsLoaded=true;
        }

    }
    void downloadDP(final int id, final Callable<Void> onFailure,final Callable<Void> onSuccess)
    {
        final ImageView tempIV=new ImageView(this);
        AppToServer.downloadFileImage(this, AdditionalFunctions.getDP_URL(id, true), tempIV,new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                //No.. of Retry
            if(onFailure!=null)
                onFailure.call();
                return null;
            }
        },new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if(onSuccess!=null)
                {
                    onSuccess.call();
                }
                changeLocalDP(id,tempIV.getDrawable());

          return null;
            }
        });

    }
    void changeLocalDP(int id,Drawable img)
    {
        int pos=arrayAdapter.getPositionById(id);
        if(pos<0) return;

        userinfo_element element=((userinfo_element)arrayAdapter.getItem(pos));
        /************Not Working************/
        element.setBitmapDp(img);
        ImageView iv=(element).getImageView_dp();
        if(iv!=null)
            iv.setImageDrawable(img);
    }
    @Override
    protected void onResume() {
        super.onResume();
        refreshLastMessages();
        isRunningForeground=true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunningForeground=false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(this, SettingsActivity.class);
            startActivity(in);
            return true;
        } else if (id == R.id.action_logout) {
            AppToServer.doLogout(this);
            return true;
        } else if (id == R.id.action_changeDP) {
            takePictureFromCamera();
        } else if (id == R.id.action_changeNname) {
            changeNickName();
        } else if (id == R.id.action_changePwd) {
            changePassword();
        } else if (id == R.id.action_showDP) {
            ImageActivity.startMe(this,AdditionalFunctions.getDP_URL(AppToServer.getId(),false),AppToServer.getNickName());
        }


        return super.onOptionsItemSelected(item);
    }


    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private File  myDPfile=null;
    public static File getTempImageFile(String PREFIX,String ext)
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = PREFIX+"_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image=null;
        try {
            image= File.createTempFile(
                    imageFileName,  /* prefix */
                    "."+ext,         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
    private void takePictureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            myDPfile=getTempImageFile("DP","jpg");
            if(myDPfile==null)
            {
                Toast.makeText(this, "Error in Creating Temp. File!", Toast.LENGTH_SHORT).show();
                return;
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(myDPfile));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else
            Toast.makeText(this, "No App Camera Found!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                final Activity activity=this;
                AdditionalFunctions.uploadFileDP(this,scaleToDPSize(myDPfile,640,480),scaleToDPSize(myDPfile,128,96),new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if(myDPfile.exists())myDPfile.delete();
                        Toast.makeText(getApplicationContext(),"DP Changed!",Toast.LENGTH_SHORT).show();
                        downloadDP(AppToServer.getId(),null,null);
                        //AppToServer.downloadFileImage(activity, AdditionalFunctions.getDP_URL(AppToServer.getId(), true), tv_myDP);
                        return null;
                    }
                });

            }
            else
                if(resultCode == RESULT_CANCELED)
                {
                    Toast.makeText(this,"Cancelled By User",Toast.LENGTH_SHORT).show();
                }
        }
    }
    Bitmap scaleToDPSize(File file,int targetW,int targetH)
    {

        //Scale The Image

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        if(photoH<150 ||photoW<100 )
        {
            Toast.makeText(this,"Image Resolution is too small.",Toast.LENGTH_SHORT).show();
            return null;
        }

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        return  bitmap;
    }

    void changeNickName()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        final EditText editText=new EditText(this);
        alertDialog.setTitle("Nickname");
        alertDialog.setView(editText);
        editText.setText(AppToServer.getNickName());
        editText.setHint("< nickname >");
        editText.setSingleLine();
        alertDialog.setNegativeButton("No Change",null);
        final Activity activity=this;
        alertDialog.setPositiveButton("Change",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppToServer.changeMyDetails(activity, editText.getText().toString(), null, null,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        setOfflineData();
                        return null;
                    }
                });
            }
        });
        alertDialog.show();
    }
    void changePassword()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        LinearLayout ll=new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        final EditText editText_old=new EditText(this);
        final EditText editText_new1=new EditText(this);
        final EditText editText_new2=new EditText(this);
        alertDialog.setTitle("Enter New Password");
        alertDialog.setView(ll);
        editText_old.setHint("< Old Password >");
        editText_old.setSingleLine();
        editText_new1.setHint("< New Password >");
        editText_new2.setHint("< Confirm Password >");
        editText_new1.setSingleLine();
        editText_new2.setSingleLine();
        editText_old.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText_new1.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText_new2.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ll.addView(editText_old);
        ll.addView(editText_new1);
        ll.addView(editText_new2);

        alertDialog.setNegativeButton("No Change",null);
        final Activity activity=this;
        alertDialog.setPositiveButton("Change",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!editText_new1.getText().toString().equals(editText_new2.getText().toString()))
                    Toast.makeText(activity,"Password Didn't Match Try Again!",Toast.LENGTH_SHORT).show();
                else
                AppToServer.changeMyDetails(activity,null,editText_old.getText().toString(),editText_new1.getText().toString(),new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        Toast.makeText(activity,"Password Changed",Toast.LENGTH_SHORT).show();
                        return null;
                    }
                });
            }
        });
        alertDialog.show();
    }


}
class userinfo_element
{
    private String uname,nname,lastMessage="";
    private String lastMsgTime="";
    private int id,lastMessageType;
    private boolean isOnline=false,lastMsgIsSend;
    private Drawable dp;
    private ImageView imageView_dp;
    userinfo_element(String _uname,String _nname,int _id,boolean _isOnline,Drawable _dp,String _lMsg,int lMsgType,boolean lMsgisSend)
    {
        uname=_uname;
        nname=_nname;
        id=_id;
        isOnline=_isOnline;
        dp=_dp;
        lastMessage=_lMsg;
        lastMessageType=lMsgType;
        lastMsgIsSend=lMsgisSend;
        lastMsgTime="";
    }



    int getLastMessageType()
    {
        return lastMessageType;
    }
    boolean getIsLastMessageSend()
    {
        return lastMsgIsSend;
    }
    String getLastMessage()
    {
        return lastMessage;
    }
    String getLastMsgTime(){return lastMsgTime;}
    void setLastMessage(String msg,boolean isSend,int type,String time)
    {
        lastMessage=msg;
        lastMessageType=type;
        lastMsgIsSend=isSend;
        lastMsgTime=time;

    }
    void setIsOnline(boolean isOnline)
    {
        this.isOnline=isOnline;
    }
    String getUname()
    {return  uname;}
    String getNname()
    {return  nname;}
    int getId(){return id;}
    boolean getIsOnline(){return isOnline;}
    Drawable getDp(){return dp;}
    void setImageView_dp(ImageView imageView)
    {
        imageView_dp=imageView;
    }
    void setBitmapDp(Drawable bitmap)
    {
        dp=bitmap;
    }
    ImageView getImageView_dp()
    {
        return imageView_dp;
    }

}
class CustomAdapterUserRoomLV extends ArrayAdapter<userinfo_element> implements View.OnClickListener {
    Context context;

    CustomAdapterUserRoomLV(Context _context)
    {
        super(_context,R.layout.layout_userlist_element);
        context=_context;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public int getPositionById(int id) {
        for(int i=0;i<getCount();i++)
            if(getItem(i).getId()==id)
                return i;
        return -1;
    }

    @Override
    public void onClick(View v) {
      Integer id;
     if((id= (Integer) v.getTag())!=null) {
         int otherPosInlist = AppToServer.getAllUserId().indexOf(id);
         if (otherPosInlist >= 0) {
             ImageActivity.startMe(context, AdditionalFunctions.getDP_URL(id, false), AppToServer.getAllUserNname().get(otherPosInlist));

         }
     }
    }

    private class ViewHolder{
        ImageView iv_dp;
        TextView tv_nname,tv_lastMsg,tv_lastMsgTime;
        ImageView iv_isOnline,iv_MsgType,iv_isSend;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(convertView==null)
        {
            convertView=inflater.inflate(R.layout.layout_userlist_element,null);
            viewHolder=new ViewHolder();
            viewHolder.iv_dp= (ImageView) convertView.findViewById(R.id.id_userhome_iv_listUser_dp);
            viewHolder.iv_dp.setOnClickListener(this);
            viewHolder.tv_nname=(TextView) convertView.findViewById(R.id.id_userhome_tv_listUser_text);
            viewHolder.iv_isOnline= (ImageView) convertView.findViewById(R.id.id_userhome_iv_listUser_isOnline);
            viewHolder.iv_MsgType=(ImageView)convertView.findViewById(R.id.id_userhome_iv_listUser_lastMessageType);
            viewHolder.tv_lastMsg=(TextView)convertView.findViewById(R.id.id_userhome_tv_listUser_lastMessage);
            viewHolder.tv_lastMsgTime= (TextView) convertView.findViewById(R.id.id_userhome_tv_listUser_lastMessageTime);
            viewHolder.iv_isSend=(ImageView)convertView.findViewById(R.id.id_userhome_iv_listUser_lastMessageIsSend);

            viewHolder=setViewHolder(viewHolder,position);

            convertView.setTag(viewHolder);

        }
        else
        {
            viewHolder=setViewHolder((ViewHolder) convertView.getTag(),position);
            convertView.setTag(viewHolder);

        }
        return convertView;
    }
    public ViewHolder setViewHolder(ViewHolder viewHolder,int position)
    {
        userinfo_element element=getItem(position);
        element.setImageView_dp(viewHolder.iv_dp);
        if(element.getDp()!=null)
            viewHolder.iv_dp.setImageDrawable(element.getDp());
        else
            viewHolder.iv_dp.setImageResource(UserRoom.resource_noDP);
        viewHolder.iv_dp.setTag(element.getId());


        if(element.getIsOnline())
            viewHolder.iv_isOnline.setAlpha(1.0f);
        else
            viewHolder.iv_isOnline.setAlpha(0.0f);

        viewHolder.tv_nname.setText(element.getNname());
        if(element.getLastMessageType()>=0)
        {
            if(element.getLastMessageType()>0)
                viewHolder.iv_MsgType.setVisibility(View.VISIBLE);
            else
                viewHolder.iv_MsgType.setVisibility(View.GONE);

            viewHolder.tv_lastMsg.setText(element.getLastMessage());
            if(!element.getIsLastMessageSend())
                viewHolder.iv_isSend.setVisibility(View.GONE);
            else
                viewHolder.iv_isSend.setVisibility(View.VISIBLE);

        }
        else
        {
            viewHolder.iv_MsgType.setVisibility(View.GONE);
            viewHolder.tv_lastMsg.setText("");
        }
        if(!element.getLastMsgTime().isEmpty()) {
            String S = TimeConvertor.getShortTime(element.getLastMsgTime());

            viewHolder.tv_lastMsgTime.setText(S);
            if (S.equals("Now")) {
                viewHolder.tv_lastMsg.setTextColor(Color.BLACK);
                viewHolder.tv_lastMsgTime.setTextColor(Color.BLACK);
            } else {
                viewHolder.tv_lastMsg.setTextColor(Color.GRAY);
                viewHolder.tv_lastMsgTime.setTextColor(Color.GRAY);

            }
        } else {
            viewHolder.tv_lastMsg.setTextColor(Color.GRAY);
            viewHolder.tv_lastMsgTime.setTextColor(Color.GRAY);
            viewHolder.tv_lastMsgTime.setText("");
        }
        return viewHolder;

    }
}
class OnlineTestThread implements Runnable
{
    static Thread t;
    int REFRESH_TIMER=30*1000;
    boolean STOP_TESTING=false;
    UserRoom parent;
    OnlineTestThread(UserRoom activity)
    {
        parent=activity;
        if(t!=null)
        {
            t.stop();
        }
        t=new Thread(this);
        t.start();
    }
    @Override
    public void run() {
        try {
            while(!STOP_TESTING) {
                AppToServer.justPingServer();
                if(parent.isRunningForeground)AppToServer.refreshOnline(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        parent.refreshAdapterOnline();
                        return null;
                    }
                });
                t.sleep(REFRESH_TIMER);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
