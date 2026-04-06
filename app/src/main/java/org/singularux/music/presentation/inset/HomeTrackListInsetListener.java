package org.singularux.music.presentation.inset;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

public class HomeTrackListInsetListener implements OnApplyWindowInsetsListener {

    private static final int PADDING_TOP_DP = 64 + 8;  // Search Bar
    private static final int PADDING_BOTTOM_DP = 0;  // TODO: Add FAB and Playback Bar

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
        // Retrieve required insets
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        float density = view.getContext().getResources().getDisplayMetrics().density;
        // Apply padding
        int paddingLeft = insets.left;
        int paddingRight = insets.right;
        int paddingTop = (int) (density * PADDING_TOP_DP) + insets.top;
        int paddingBottom = (int) (density * PADDING_BOTTOM_DP) + insets.bottom;
        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        // Last element of the view
        return WindowInsetsCompat.CONSUMED;
    }

}
