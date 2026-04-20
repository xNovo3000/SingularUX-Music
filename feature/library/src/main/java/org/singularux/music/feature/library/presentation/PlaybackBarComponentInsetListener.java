package org.singularux.music.feature.library.presentation;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;

public class PlaybackBarComponentInsetListener implements OnApplyWindowInsetsListener {

    @Override
    @NonNull
    public WindowInsetsCompat onApplyWindowInsets(@NonNull View view,
                                                  @NonNull WindowInsetsCompat windowInsets) {
        // Retrieve required insets
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
        // Apply padding
        view.setPadding(insets.left, 0, insets.right, insets.bottom);
        // Last element of the view
        return WindowInsetsCompat.CONSUMED;
    }

}
