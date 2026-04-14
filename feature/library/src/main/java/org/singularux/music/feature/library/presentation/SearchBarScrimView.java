package org.singularux.music.feature.library.presentation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SearchBarScrimView extends View {

    private int statusBarHeight = 0;

    public SearchBarScrimView(Context context) {
        super(context);
        init();
    }

    public SearchBarScrimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchBarScrimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Request to listen for window insets when view is attached
        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            statusBarHeight = systemInsets.top;
            requestLayout();
            return insets;
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        // Set measured dimensions: width = match_parent, height = status bar height
        setMeasuredDimension(parentWidth, statusBarHeight);
    }

}