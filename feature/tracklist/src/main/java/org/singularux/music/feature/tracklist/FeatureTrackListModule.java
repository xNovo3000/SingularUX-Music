package org.singularux.music.feature.tracklist;

import android.os.Build;

import org.singularux.music.feature.tracklist.ui.search.SearchViewOnBackPressedCallback;
import org.singularux.music.feature.tracklist.ui.search.SearchViewTransitionObserver;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(FragmentComponent.class)
public class FeatureTrackListModule {

    @Provides
    @FragmentScoped
    public SearchViewOnBackPressedCallback providesSearchViewOnBackPressedCallback() {
        return new SearchViewOnBackPressedCallback();
    }

    @Provides
    @FragmentScoped
    public SearchViewTransitionObserver providesSearchViewTransitionListener(
            SearchViewOnBackPressedCallback onBackPressedCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return new SearchViewTransitionObserver.Disabled();  // Using predictive back
        } else {
            return new SearchViewTransitionObserver.Enabled(onBackPressedCallback);
        }
    }

}
