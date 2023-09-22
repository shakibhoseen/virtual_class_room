package com.example.myapplication.group_chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreMsgHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MessageStore.db";
    public static final String TABLE_NAME = "msgcontent_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "sender";
    public static final String COL_3 = "message";
    public static final String COL_4 = "totalseen";
    public static final String COL_5 = "FireId";
    public static final String COL_6 = "isseen";
    public static final String COL_7 = "publish";



    public StoreMsgHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT, " + COL_3 + " TEXT," +
                COL_4 + " TEXT ,"+ COL_5 + " TEXT ,"+COL_6 + " TEXT ,"+ COL_7 +" INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData(String sender, String message, String totalseen, String FireId, long publish) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, sender);
        contentValues.put(COL_3, message);
        contentValues.put(COL_4, totalseen);
        contentValues.put(COL_5, FireId);
        contentValues.put(COL_7, publish);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else return true;
    }

    /*public boolean delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "ID = ?", new String[]{id});
        if (result != 0) return true;
        else return false;
    }*/

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }

    public Cursor getAlldata(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * From "+TABLE_NAME, null);
    }


    public boolean upDtSeenNumber(String id, String totalSeen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(COL_4, totalSeen );
         db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
      return true;
    }


}
