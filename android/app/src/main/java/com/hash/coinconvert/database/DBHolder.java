package com.hash.coinconvert.database;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import androidx.room.Room;

import com.hash.coinconvert.database.dao.QuoteDao;
import com.hash.coinconvert.database.dao.TokenDao;
import com.hash.coinconvert.database.entity.Token;
import com.hash.coinconvert.database.repository.TokenRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class DBHolder {

    private static final String DB_NAME = "hash_v2.db";

    private DB db;

    private static class Holder {
        private static final DBHolder instance = new DBHolder();
    }

    private DBHolder() {
    }

    public static DBHolder getInstance() {
        return Holder.instance;
    }

    public QuoteDao quoteDao() {
        return db.quoteDao();
    }

    public TokenDao tokenDao() {
        return db.tokenDao();
    }

    public static void init(Application application) {
        getInstance().init_(application);
    }

    private void init_(Application application) {
        if (db == null) {
            synchronized (DBHolder.class) {
                db = Room.databaseBuilder(application, DB.class, DB_NAME)
                        .build();
            }
        }
    }

    /**
     * copy data from csv first.
     */
    public Observable<Boolean> prepareTokenAsync(Context context) {
        return Observable.create(sub -> {
            if (TokenRepository.getDataCount() == 0) {
                try {
                    List<Token> list = prepareTokensSync(context);
                    TokenRepository.insertTokens(list);
                    sub.onNext(true);
                } catch (Exception e) {
                    sub.onError(e);
                }
            } else {
                sub.onNext(true);
            }
            sub.onComplete();
        });
    }

    private List<Token> prepareTokensSync(Context context) {
        AssetManager am = context.getAssets();
        try {
            InputStream inputStream = am.open("db/currency.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            List<Token> tokens = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                Token token = Token.fromCSVItem(line);
                if (token != null) {
                    tokens.add(token);
                }
            }
            return tokens;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
