package com.rate.quiz.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.rate.quiz.database.dao.QuoteDao;
import com.rate.quiz.database.dao.TokenDao;
import com.rate.quiz.database.entity.Quote;
import com.rate.quiz.database.entity.Token;

@Database(entities = {Quote.class, Token.class}, version = 1, exportSchema = true)
public abstract class DB extends RoomDatabase {
    public abstract QuoteDao quoteDao();

    public abstract TokenDao tokenDao();
}
