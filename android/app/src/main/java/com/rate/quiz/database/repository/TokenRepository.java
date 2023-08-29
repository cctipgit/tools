package com.rate.quiz.database.repository;

import com.rate.quiz.database.DBHolder;
import com.rate.quiz.database.dao.TokenDao;
import com.rate.quiz.database.entity.Token;

import java.util.List;

public class TokenRepository {

    public static void insertTokens(List<Token> tokens) {
        DBHolder.getInstance().tokenDao().add(tokens);
    }

    public static int getDataCount() {
        return DBHolder.getInstance().tokenDao().getDataCount();
    }

    public static List<Token> all() {
        return DBHolder.getInstance().tokenDao().all();
    }

    public static List<Token> getFavorites() {
        return DBHolder.getInstance().tokenDao().getFavorites();
    }

    public static void updatePrice(String token, String price) {
        DBHolder.getInstance().tokenDao().updatePrice(token, price);
    }

    public static void updateFavorite(String token, boolean fav) {
        DBHolder.getInstance().tokenDao().updateFavorite(token, fav ? TokenDao.FAVORITE : 0);
    }

    public static List<Token> queryByCurrencyType(String currencyType) {
        return DBHolder.getInstance().tokenDao().queryByCurrencyType(currencyType);
    }

    public static Token findBySymbol(String symbol) {
        return DBHolder.getInstance().tokenDao().findBySymbol(symbol);
    }
}
