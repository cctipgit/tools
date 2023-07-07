package com.hash.coinconvert.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.hash.coinconvert.database.entity.Quote;

import java.util.List;

@Dao
public interface QuoteDao {
    @Query("DELETE FROM tb_quote WHERE tokenFrom=:tokenFrom AND tokenTo=:tokenTo AND dateUnit=:dateUnit and childPriceDate=:childPriceDate")
    void clearByTokenPairPoint(String tokenFrom, String tokenTo, String dateUnit, String childPriceDate);


    @Query("DELETE FROM tb_quote WHERE tokenFrom=:tokenFrom AND tokenTo=:tokenTo AND dateUnit=:dateUtil")
    void clearByTokenPair(String tokenFrom, String tokenTo, String dateUtil);

    @Query("SELECT * FROM tb_quote WHERE tokenFrom=:tokenFrom AND tokenTo=:tokenTo AND dateUnit=:dateUnit")
    List<Quote> queryQuotesListByCategoryName(String tokenFrom, String tokenTo, String dateUnit);

    @Query("DELETE FROM tb_quote")
    void clear();

    @Insert
    void add(List<Quote> list);
}
