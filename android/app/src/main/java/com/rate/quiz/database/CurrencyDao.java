package com.rate.quiz.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.rate.quiz.entity.CurrencyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 货币数据库DAO
 */
public class CurrencyDao {

    private final String TAG = "CurrencyDao";
    private SQLiteDatabase mReadableDatabase;
    private SQLiteDatabase mWriteableDatabase;
    private DBHelper mDBHelper;

    public CurrencyDao() {
        mDBHelper = new DBHelper(Utils.getApp());
    }

    private SQLiteDatabase getReadableDatabase() {
        if (mReadableDatabase == null) {
            synchronized (CurrencyDao.this) {
                if (mReadableDatabase == null) {
                    mReadableDatabase = mDBHelper.getReadableDatabase();
                }
            }
        }
        return mReadableDatabase;
    }

    private SQLiteDatabase getWriteableDatabase() {
        if (mWriteableDatabase == null) {
            synchronized (CurrencyDao.this) {
                if (mWriteableDatabase == null) {
                    mWriteableDatabase = mDBHelper.getWritableDatabase();
                }
            }
        }
        return mWriteableDatabase;
    }

    public void close() {
        if (mWriteableDatabase != null) {
            if (mWriteableDatabase.isOpen()) {
                mWriteableDatabase.close();
            }
            mWriteableDatabase = null;
        }

        if (mReadableDatabase != null) {
            if (mReadableDatabase.isOpen()) {
                mReadableDatabase.close();
            }
            mReadableDatabase = null;
        }

        if (mDBHelper != null) {
            mDBHelper.close();
            mDBHelper = null;
        }
    }

    /**
     * 添加货币
     *
     * @param list
     */
    public void insert(List<CurrencyInfo> list) {
        if (EmptyUtils.isEmpty(list)) {
            return;
        }

        SQLiteDatabase db = getWriteableDatabase();
        db.beginTransaction();
        try {
            for (CurrencyInfo item : list) {
                db.insert(DBHelper.CURRENCY_TABLE_NAME, null, newContentValues(item));
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 添加货币
     *
     * @param currencyInfo
     */
    public void insert(CurrencyInfo currencyInfo) {
        if (currencyInfo == null) {
            return;
        }
        SQLiteDatabase db = getWriteableDatabase();
        db.insert(DBHelper.CURRENCY_TABLE_NAME, null, newContentValues(currencyInfo));
    }

    /**
     * 修改价格
     * @param list
     */
    public void updatePrice(List<CurrencyInfo> list) {
        if(EmptyUtils.isEmpty(list)) {
            return;
        }
        SQLiteDatabase db = getWriteableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            String where = DBHelper.CURRENCY_TOKEN + "='%s'";
            for (CurrencyInfo currencyInfo : list) {
                values.clear();
                values.put(DBHelper.CURRENCY_PRICE, currencyInfo.price);
                db.update(DBHelper.CURRENCY_TABLE_NAME, values, String.format(where, currencyInfo.token), null);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 删除数据
     *
     * @param list
     */
    @SuppressLint("NewApi")
    public void delete(List<CurrencyInfo> list) {
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        deleteByTokens(list.stream().map(currencyInfo -> currencyInfo.token).collect(Collectors.toList()));
    }

    /**
     * 根据指定的token删除数据
     *
     * @param tokens
     */
    public void deleteByTokens(List<String> tokens) {
        if (EmptyUtils.isEmpty(tokens)) {
            return;
        }

        SQLiteDatabase db = getWriteableDatabase();
        String whereClause = String.format(DBHelper.CURRENCY_TOKEN + " in(%s)", connectTokenIn(tokens));
        Log.i("duxl.log", "sql删除条件：" + whereClause);
        db.delete(DBHelper.CURRENCY_TABLE_NAME, whereClause, null);
    }

    /**
     * 获取所有的货币
     *
     * @return
     */
    public List<CurrencyInfo> getCurrencyAll() {
        return getCurrencyByTokens(null);
    }

    /**
     * 根据指定tokens获取货币
     *
     * @param tokens
     * @return
     */
    @SuppressLint("Range")
    public List<CurrencyInfo> getCurrencyByTokens(List<String> tokens) {
        return getCurrencyByTokens(getReadableDatabase(), tokens);
    }

    /**
     * 根据指定tokens获取货币
     *
     * @param tokens
     * @return
     */
    @SuppressLint("Range")
    public List<CurrencyInfo> getCurrencyByTokens(SQLiteDatabase db, List<String> tokens) {
        List<CurrencyInfo> result = new ArrayList<>();
        String selection = null;
        if (EmptyUtils.isNotEmpty(tokens)) {
            selection = String.format(DBHelper.CURRENCY_TOKEN + " in(%s)", connectTokenIn(tokens));
        }
        Log.i(TAG, "select selection: " + selection);
        Cursor cursor = db.query(DBHelper.CURRENCY_TABLE_NAME, DBHelper.CURRENCY_PROJECTION, selection, null, null, null, DBHelper.CURRENCY_FCHAT);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CurrencyInfo item = new CurrencyInfo();
                item.token = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_TOKEN));
                item.icon = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_ICON));
                item.name = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_NAME));
                item.currencyType = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_CURRENCYTYPE));
                item.unitName = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_UNITNAME));
                item.countryCode = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_COUNTRYCODE));
                item.fChat = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_FCHAT));
                item.price = cursor.getString(cursor.getColumnIndex(DBHelper.CURRENCY_PRICE));
                result.add(item);
            }
            cursor.close();
        }
        return result;
    }

    private ContentValues newContentValues(CurrencyInfo currencyInfo) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.CURRENCY_TOKEN, currencyInfo.token);
        values.put(DBHelper.CURRENCY_ICON, currencyInfo.icon);
        values.put(DBHelper.CURRENCY_NAME, currencyInfo.name);
        values.put(DBHelper.CURRENCY_CURRENCYTYPE, currencyInfo.currencyType);
        values.put(DBHelper.CURRENCY_UNITNAME, currencyInfo.unitName);
        values.put(DBHelper.CURRENCY_COUNTRYCODE, currencyInfo.countryCode);
        values.put(DBHelper.CURRENCY_FCHAT, currencyInfo.fChat);
        values.put(DBHelper.CURRENCY_PRICE, currencyInfo.price);
        return values;
    }

    /**
     * 连接token成为sql的in条件
     *
     * @param tokens
     * @return
     */
    private String connectTokenIn(List<String> tokens) {
        StringBuilder sb = new StringBuilder("'" + tokens.get(0) + "'");
        for (int i = 1; i < tokens.size(); i++) {
            sb.append(",'");
            sb.append(tokens.get(i));
            sb.append("'");
        }
        return sb.toString();
    }

}