package com.jaredhuang.bookdb.widget.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.jaredhuang.bookdb.utils.ColorUtil;
import com.jaredhuang.bookdb.utils.ScreenUtils;
import com.jaredhuang.bookdb.utils.Selector;
import com.jaredhuang.bookdb.utils.theme.ThemeStore;

public class ATEAccentBgTextView extends AppCompatTextView {
    public ATEAccentBgTextView(Context context) {
        super(context);
        init(context, null);
    }

    public ATEAccentBgTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ATEAccentBgTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackground(Selector.shapeBuild()
                .setCornerRadius(ScreenUtils.dpToPx(3))
                .setDefaultBgColor(ThemeStore.accentColor(context))
                .setPressedBgColor(ColorUtil.darkenColor(ThemeStore.accentColor(context)))
                .create());
        setTextColor(Color.WHITE);
    }
}
