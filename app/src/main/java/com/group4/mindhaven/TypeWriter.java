package com.group4.mindhaven;

import android.os.Handler;
import android.widget.TextView;

public class TypeWriter {
    private TextView textView;
    private String text;
    private int index;
    private long delay = 30; // Delay between each character in milliseconds

    public TypeWriter(TextView textView) {
        this.textView = textView;
    }

    public void animateText(String text) {
        this.text = text;
        index = 0;

        textView.setText("");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < text.length()) {
                    textView.setText(text.substring(0, index + 1));
                    index++;
                    new Handler().postDelayed(this, delay);
                }
            }
        }, delay);
    }

    public void setCharacterDelay(long delay) {
        this.delay = delay;
    }
} 