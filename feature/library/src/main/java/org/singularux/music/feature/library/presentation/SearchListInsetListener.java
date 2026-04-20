package org.singularux.music.feature.library.presentation;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

public class SearchListInsetListener implements OnApplyWindowInsetsListener {

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view, @NonNull WindowInsetsCompat windowInsets) {
        // Retrieve required insets
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        // Apply padding
        view.setPadding(insets.left, 0, insets.right, 0);
        // Last element of the view
        return WindowInsetsCompat.CONSUMED;
    }
    
}
