package com.rate.quiz.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.duxl.baselib.utils.Utils;
import com.rate.quiz.entity.CurrencyInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 数据库初始化类
 * create by duxl 2023/1/6
 */
public class DBInit {

    private static final String TAG = "DBInit";
    private static final String dbPath = "/data/data/%s/databases/db.sqlite";

    private DBInit() {
    }

    private static SQLiteDatabase getDatabase(boolean repeat) {
        File dbFile = new File(String.format(dbPath, Utils.getApp().getPackageName()));
        if (dbFile.exists()) {
            return SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
        }

        if (repeat) {
            return null;
        }

        InputStream is = null;
        OutputStream os = null;
        try {
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }

            is = Utils.getApp().getAssets().open("db/db.sqlite");
            os = new FileOutputStream(dbFile);
            byte[] buffer = new byte[512];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return getDatabase(true);
    }

    public static void init() {
        Log.i(TAG,"init database");
        SQLiteDatabase database = getDatabase(false);
        if (database == null) {
            Log.i(TAG, "init database fail, database is null");
            return;
        }

        CurrencyDao dao = new CurrencyDao();
        List<CurrencyInfo> allCurrency = dao.getCurrencyByTokens(database, null);
        database.close();

        dao.insert(allCurrency);
        dao.close();

        File dbFile = new File(String.format(dbPath, Utils.getApp().getPackageName()));
        if(dbFile.exists()) {
            dbFile.delete();
        }
        Log.i(TAG, "init database success: data count is " + allCurrency.size());
    }
}
