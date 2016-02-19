package com.example.gagan.italk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gagan on 3/7/15.
 */
public class DB extends SQLiteOpenHelper {
    public final static String DB_NAME="iTalk.db";
    public final static int DB_VERSION=1;


    public final static String DB_TABLE_USERS="USERS";
    public final static String DB_TABLE_CHAT="INFO";

    Context context;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
