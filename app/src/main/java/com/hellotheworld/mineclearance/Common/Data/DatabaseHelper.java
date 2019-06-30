package com.hellotheworld.mineclearance.Common.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_RANK = "Rank";
    public static final String TABLE_RANK_COLUMN_ID = "id";
    public static final String TABLE_RANK_COLUMN_LEVEL = "level";
    public static final String TABLE_RANK_COLUMN_NAME = "name";
    public static final String TABLE_RANK_COLUMN_TIME = "time";
    public static final String TABLE_RANK_COLUMN_SCREENSHOT = "screenShot";
    public static final String CREATE_RANK = "" +
            "create table Rank(" +
            "id integer primary key autoincrement," +
            "level integer not null," +
            "name text default '匿名'," +
            "time float not null," +
            "screenShot text)";



    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version ){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RANK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Rank");
        onCreate(db);
    }
}
