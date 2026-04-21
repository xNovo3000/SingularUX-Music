package org.singularux.music.feature.library.presentation;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

public class SearchBarInsetListener implements OnApplyWindowInsetsListener {

    private static final int MARGIN_HORIZONTAL_DP = 16;
    private static final int MARGIN_TOP_DP = 8;

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
        // Retrieve required insets
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        float density = view.getContext().getResources().getDisplayMetrics().density;
        // Apply margin
        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.topMargin = insets.top + (int) (density * MARGIN_TOP_DP);
        marginLayoutParams.leftMargin = insets.left + (int) (density * MARGIN_HORIZONTAL_DP);
        marginLayoutParams.rightMargin = insets.right + (int) (density * MARGIN_HORIZONTAL_DP);
        view.setLayoutParams(marginLayoutParams);
        // Last element of the view
        return WindowInsetsCompat.CONSUMED;
    }

}
