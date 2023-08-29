package com.rate.quiz.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.rate.quiz.database.entity.Token;

import java.util.List;


@Dao
public interface TokenDao {
    int FAVORITE = 1;

    @Query("SELECT * FROM currency WHERE favorite=1")
    List<Token> getFavorites();

    @Insert
    void add(List<Token> list);

    @Query("SELECT * FROM currency")
    List<Token> all();

    @Query("SELECT COUNT(token) from currency")
    int getDataCount();

    @Query("UPDATE currency SET price=:price WHERE token=:token")
    void updatePrice(String token, String price);

    @Query("UPDATE currency SET favorite=:favorite WHERE token=:token")
    void updateFavorite(String token, int favorite);

    @Query("SELECT * from currency WHERE currencyType=:currencyType")
    List<Token> queryByCurrencyType(String currencyType);

    @Query("SELECT * FROM currency WHERE token=:symbol")
    Token findBySymbol(String symbol);
}
