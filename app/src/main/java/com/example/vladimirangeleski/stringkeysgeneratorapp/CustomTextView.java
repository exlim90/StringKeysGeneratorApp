package com.example.vladimirangeleski.stringkeysgeneratorapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by vladimir.angeleski on 02/06/2017.
 */

public class CustomTextView extends TextView {
    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int[] set = {
            android.R.attr.text
        };
        TypedArray a = context.obtainStyledAttributes(attrs, set);
        int resId = a.getResourceId(0, R.string.app_name);
        String translationKey = getResources().getResourceEntryName(resId);
        String translatedValue = context.getString(resId);
        a.recycle();
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
