package com.hash.coinconvert.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String DATABASE_NAME = "hash.db";
    //数据库版本
    private final static int DATABASE_VERSION = 1;

    // 货币表
    public final static String CURRENCY_TABLE_NAME = "currency";

    public final static String CURRENCY_TOKEN = "token";
    public final static String CURRENCY_ICON = "icon";
    public final static String CURRENCY_NAME = "name";
    public final static String CURRENCY_CURRENCYTYPE = "currencyType";
    public final static String CURRENCY_UNITNAME = "unitName";
    public final static String CURRENCY_COUNTRYCODE = "countryCode";
    public final static String CURRENCY_FCHAT = "fchat";
    public final static String CURRENCY_PRICE = "price";

    // 货币列表字段
    public static final String[] CURRENCY_PROJECTION = new String[]{
            CURRENCY_TOKEN,
            CURRENCY_ICON,
            CURRENCY_NAME,
            CURRENCY_CURRENCYTYPE,
            CURRENCY_UNITNAME,
            CURRENCY_COUNTRYCODE,
            CURRENCY_FCHAT,
            CURRENCY_PRICE
    };

    // 创建货币类型表的sql
    public static final String CREATE_TABLE_CURRENCY = "create table if not exists " + CURRENCY_TABLE_NAME + "("
            + CURRENCY_TOKEN + " text primary key,"
            + CURRENCY_ICON + " text,"
            + CURRENCY_NAME + " text,"
            + CURRENCY_CURRENCYTYPE + " text,"
            + CURRENCY_UNITNAME + " text,"
            + CURRENCY_COUNTRYCODE + " text,"
            + CURRENCY_FCHAT + " text,"
            + CURRENCY_PRICE + " text"
            + ");";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CURRENCY);
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CURRENCY_TABLE_NAME);
        onCreate(db);
    }
}
