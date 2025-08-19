package org.singularux.music.feature.tracklist.ui.list;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TrackListInsetListener implements OnApplyWindowInsetsListener {

    private static final int INSETS_TOP_DP = 64 + 8;
    private static final int INSETS_BOTTOM_DP = 8 + 76 + 16 + 64 + 16;

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view, @NonNull WindowInsetsCompat windowInsets
    ) {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        float displayDensity = view.getContext().getResources().getDisplayMetrics().density;
        view.setPadding(
                0,
                insets.top + (int) (INSETS_TOP_DP * displayDensity),
                0,
                insets.bottom + (int) (INSETS_BOTTOM_DP * displayDensity)
        );
        return WindowInsetsCompat.CONSUMED;
    }

}
