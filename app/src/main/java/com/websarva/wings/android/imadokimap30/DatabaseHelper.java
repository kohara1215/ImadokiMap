package com.websarva.wings.android.imadokimap30;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "default.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "DataBaseTable";// テーブル名

    // 列名(column)
    public static final String COLUMN_ID = "_id";//id
    public static final String COLUMN_NAME = "_name";//店名
    public static final String COLUMN_ADDRESS = "_address";//住所
    public static final String COLUMN_HOURS = "_hours";//営業時間
    public static final String COLUMN_CATEGORY = "_category";//カテゴリー

    public DatabaseHelper(Context context){
        // 指定したデータベース名が存在しない場合、onCreate()が呼ばれる
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context,String databaseName){
        // 指定したデータベース名が存在しない場合、onCreate()が呼ばれる
        super(context, databaseName+".db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try{
            //テーブルの作成
            StringBuilder sb = new StringBuilder();
            sb.append("create table " + TABLE_NAME + " (");
            sb.append(COLUMN_ID + " integer primary key autoincrement,");//id、自動ナンバリング
            sb.append(COLUMN_NAME + " text,");//店名、string型
            sb.append(COLUMN_ADDRESS + " text,");//住所、string型
            sb.append(COLUMN_HOURS + " text,");//営業時間、string型
            sb.append(COLUMN_CATEGORY + " text");//カテゴリー、string型
            sb.append(")");
            db.execSQL(sb.toString());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    //データベースの更新
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // データベースのバージョン変更で呼び出される。
        //例：データ引継ぎ等はここに記載

    }
}
