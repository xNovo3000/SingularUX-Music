package org.singularux.music.feature.tracklist.ui.component;

import androidx.annotation.NonNull;

import com.google.android.material.search.SearchView;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchViewTransitionListener26 extends SearchViewTransitionListener {

    private final SearchViewOnBackPressedCallback onBackPressedCallback;

    @Override
    public void onStateChanged(
            @NonNull SearchView searchView,
            @NonNull SearchView.TransitionState previousState,
            @NonNull SearchView.TransitionState newState
    ) {
        switch (newState) {
            case SHOWING:
                onBackPressedCallback.setEnabled(true);
                break;
            case HIDING:
                onBackPressedCallback.setEnabled(false);
                break;
        }
    }

}
