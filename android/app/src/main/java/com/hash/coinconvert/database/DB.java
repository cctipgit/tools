package com.hash.coinconvert.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.hash.coinconvert.database.dao.QuoteDao;
import com.hash.coinconvert.database.dao.TokenDao;
import com.hash.coinconvert.database.entity.Quote;
import com.hash.coinconvert.database.entity.Token;

@Database(entities = {Quote.class, Token.class}, version = 1, exportSchema = true)
public abstract class DB extends RoomDatabase {
    public abstract QuoteDao quoteDao();

    public abstract TokenDao tokenDao();
}
