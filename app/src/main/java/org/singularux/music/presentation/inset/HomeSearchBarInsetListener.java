package org.singularux.music.presentation.inset;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

public class HomeSearchBarInsetListener implements OnApplyWindowInsetsListener {

    private static final int MARGIN_TOP_DP = 8;
    private static final int MARGIN_HORIZONTAL_DP = 16;

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
        // Retrieve required insets
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        float density = view.getContext().getResources().getDisplayMetrics().density;
        // Apply margins
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)
                view.getLayoutParams();
        marginLayoutParams.topMargin = (int) (density * MARGIN_TOP_DP) + insets.top;
        marginLayoutParams.leftMargin = (int) (density * MARGIN_HORIZONTAL_DP) + insets.left;
        marginLayoutParams.rightMargin = (int) (density * MARGIN_HORIZONTAL_DP) + insets.right;
        view.setLayoutParams(marginLayoutParams);
        // Last element of the view
        return WindowInsetsCompat.CONSUMED;
    }

}
