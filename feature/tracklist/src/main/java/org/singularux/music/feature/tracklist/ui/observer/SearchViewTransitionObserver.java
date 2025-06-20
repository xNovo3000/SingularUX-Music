package org.singularux.music.feature.tracklist.ui.observer;

import androidx.annotation.NonNull;

import com.google.android.material.search.SearchView;

import lombok.RequiredArgsConstructor;

public abstract class SearchViewTransitionObserver implements SearchView.TransitionListener {

    @RequiredArgsConstructor
    public static class Enabled extends SearchViewTransitionObserver {

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

    public static class Disabled extends SearchViewTransitionObserver {

        @Override
        public void onStateChanged(
                @NonNull SearchView searchView,
                @NonNull SearchView.TransitionState previousState,
                @NonNull SearchView.TransitionState newState
        ) {}

    }

}
