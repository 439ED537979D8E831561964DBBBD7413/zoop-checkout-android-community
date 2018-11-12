package com.zoop.checkout.app.Model;

import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by mainente on 02/01/17.
 */

public class Card {

    private String holder_name;
    private String cardBrand;
    private String numcard;
    private String CVCcard;
    private int ExpirationMonth;
    private int ExpirationMYear;
    private File imgCard;
    private int imgCard_brand;

    private static Card instance = null;


    public static Card getInstance() {
        if (null == instance) {
            instance = new Card();
        }
        return instance;
    }

    public static void setInstance(Card instance) {
        Card.instance = instance;
    }

    public String getHolder_name() {
        return holder_name;
    }

    public void setHolder_name(String holder_name) {
        this.holder_name = holder_name;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getNumcard() {
        return numcard;
    }

    public void setNumcard(String numcard) {
        this.numcard = numcard;
    }

    public String getCVCcard() {
        return CVCcard;
    }

    public void setCVCcard(String CVCcard) {
        this.CVCcard = CVCcard;
    }

    public int getExpirationMonth() {
        return ExpirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        ExpirationMonth = expirationMonth;
    }

    public int getExpirationMYear() {
        return ExpirationMYear;
    }

    public void setExpirationMYear(int expirationMYear) {
        ExpirationMYear = expirationMYear;
    }

    public File getImgCard() {
        return imgCard;
    }

    public void setImgCard(File imgCard) {
        this.imgCard = imgCard;
    }

    public int getImgCard_brand() {
        return imgCard_brand;
    }

    public void setImgCard_brand(int imgCard_brand) {
        this.imgCard_brand = imgCard_brand;
    }
}
