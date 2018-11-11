package com.example.dell.textdetectionproject;

import android.graphics.Rect;

/**
 * Created by DELL on 2018/3/22.
 */

public class POI {
    private Rect rect;
    private String text;

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
