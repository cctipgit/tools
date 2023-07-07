package com.hash.coinconvert.entity;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hash.coinconvert.database.entity.Token;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TokenWrapper {

    public TokenWrapper(@NonNull Token token) {
        this.token = token;
    }

    private Token token;

    public BigDecimal input = BigDecimal.ONE;
    private BigDecimal price = null;

    public void setToken(Token token) {
        this.token = token;
        price = null;
    }

    public Token getToken() {
        return token;
    }

    public String getLogo() {
        return token.icon;
    }

    public String getSymbol() {
        return token.token;
    }

    public boolean isCurrency() {
        return Token.TOKEN_TYPE_CURRENCY.equals(token.currencyType);
    }

    public String getName() {
        return token.name;
    }

    public BigDecimal getPrice() {
        if (price == null) {
            price = new BigDecimal(token.price);
        }
        return price;
    }

    public boolean hasFocus;

    public BigDecimal priceOfAnchor(TokenWrapper anchorToken) {
        if (anchorToken == null
                || !anchorToken.hasFocus
                || anchorToken.getPrice().compareTo(BigDecimal.ZERO) == 0
                || this == anchorToken) {
            return getPrice();
        }
        return anchorToken.input.multiply(anchorToken.getPrice()).divide(getPrice(), RoundingMode.DOWN);
    }

    public BigDecimal priceOfAnchor(TokenWrapper anchorToken, int decimal) {
        return priceOfAnchor(anchorToken).setScale(decimal, RoundingMode.DOWN);
    }
}
