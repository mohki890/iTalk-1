package com.example.gagan.italk;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Callable;


public class MainActivity extends ActionBarActivity {
    static final String LOG = MainActivity.class.getSimpleName();
    ContentConversion contentConversion;
    private static EditText et_pwd,et_username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentConversion=new ContentConversion();
        et_username=(EditText)findViewById(R.id.id_et_username);
        et_pwd=(EditText)findViewById(R.id.id_et_pwd);
        final Activity activity=this;
        findViewById(R.id.id_b_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppToServer.tryLogin(activity, et_username.getText().toString(), et_pwd.getText().toString(),new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        Intent in=new Intent(activity,UserRoom.class);
                        activity.startActivity(in);
                        activity.finish();

                        return null;
                    }
                });
            }
        });

        AppToServer.testLoggedIn(activity,new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Intent in=new Intent(activity,UserRoom.class);
                activity.startActivity(in);

                activity.finish();

                return null;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
       }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent in=new Intent(this,SettingsActivity.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}