package org.singularux.music.feature.tracklist.ui.search;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.WindowInsetsCompat;

import org.jspecify.annotations.NonNull;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SearchListInsetListener implements OnApplyWindowInsetsListener {

    @Override
    public @NonNull WindowInsetsCompat onApplyWindowInsets(
            @NonNull View view, @NonNull WindowInsetsCompat windowInsets
    ) {
        Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
        view.setPadding(0, 0, 0, insets.bottom);
        return WindowInsetsCompat.CONSUMED;
    }

}
