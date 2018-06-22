package com.kannada.android;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

public class KannadaKeyboardView extends KeyboardView {

    static final int KEYCODE_OPTIONS = -100;
    Context cont;
    public KannadaKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        cont = context;
    }

    public KannadaKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        cont = context;
        
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }
}
