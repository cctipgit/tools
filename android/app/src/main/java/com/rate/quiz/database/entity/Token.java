package com.rate.quiz.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.rate.quiz.database.dao.TokenDao;

@Entity(tableName = "currency")
public class Token {

    public static final String TOKEN_TYPE_CURRENCY = "currency";
    public static final String TOKEN_TYPE_DIGITAL = "digital";

    @StringDef({TOKEN_TYPE_CURRENCY, TOKEN_TYPE_DIGITAL})
    public @interface TokenType {
    }

    @PrimaryKey
    @NonNull
    public String token;
    @NonNull
    public String name;
    @NonNull
    public String icon;
    @NonNull
    @TokenType
    public String currencyType;
    public String unitName;
    public String countryCode;
    public String fchat;
    @NonNull
    public String price;
    @ColumnInfo(defaultValue = "0")
    public int favorite;

    public static Token fromCSVItem(String line) {
        String[] arr = line.split(",");
        //9 is fields count
        if (arr.length == 9) {
            Token token = new Token();
            token.token = arr[0];
            token.icon = arr[1];
            token.name = arr[2];
            token.currencyType = arr[3];
            token.unitName = arr[4];
            token.countryCode = arr[5];
            token.fchat = arr[6];
            token.price = arr[7];
            token.favorite = Integer.parseInt(arr[8]);
            return token;
        }
        return null;
    }

    public boolean isFavorite() {
        return favorite == TokenDao.FAVORITE;
    }

    public void revertFavorite() {
        favorite = TokenDao.FAVORITE - favorite;
    }
}
