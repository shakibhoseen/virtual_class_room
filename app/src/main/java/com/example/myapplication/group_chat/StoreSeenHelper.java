package com.example.myapplication.group_chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoreSeenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MsgSeenData.db";
    public static final String TABLE_NAME = "msgSeen_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "name";           //seen user name
    public static final String COL_3 = "ownImg";       // seen user img url
    public static final String COL_4 = "userId";       // seen  user id
    public static final String COL_5 = "contentId";   ///msg id from firebase




    public StoreSeenHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT, " + COL_3 + " TEXT," +
                COL_4 + " TEXT," +COL_5 + " TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if exists " + TABLE_NAME);
        onCreate(db);
    }


    public boolean insertData(String name, String ownImg, String userId, String contentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, ownImg);
        contentValues.put(COL_4, userId);
        contentValues.put(COL_5, contentId);

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


   /* public boolean update(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put(COL_6, "1" );
         db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
      return true;
    }*/


    public Cursor getAscSeenList(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * From "+ TABLE_NAME + " ORDER BY "+ COL_5 +" ASC", null );
    }

}
