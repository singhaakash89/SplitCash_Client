package com.app.splitCash.widUappException;

/**
 * Created by Aakash Singh on 27-07-2016.
 */
public class WidUappInvalidInputException extends IllegalArgumentException
{
    public WidUappInvalidInputException(String detailMessage) {
        super(detailMessage);
    }
}
