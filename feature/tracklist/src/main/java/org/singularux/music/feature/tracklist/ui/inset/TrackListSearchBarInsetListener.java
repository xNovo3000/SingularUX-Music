package org.singularux.music.feature.tracklist.ui.inset;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class TrackListSearchBarInsetListener implements OnApplyWindowInsetsListener {

    private static final int INSETS_TOP_DP = 8;

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view,
            @NonNull WindowInsetsCompat windowInsets
    ) {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars());
        float displayDensity = view.getContext().getResources().getDisplayMetrics().density;
        ViewGroup.MarginLayoutParams marginLayoutParams =
                (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        marginLayoutParams.topMargin = insets.top + (int) (INSETS_TOP_DP * displayDensity);
        view.setLayoutParams(marginLayoutParams);
        return WindowInsetsCompat.CONSUMED;
    }

}
