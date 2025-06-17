package org.singularux.music.feature.tracklist;

import android.os.Build;

import org.singularux.music.feature.tracklist.ui.component.SearchViewOnBackPressedCallback;
import org.singularux.music.feature.tracklist.ui.component.SearchViewTransitionListener;
import org.singularux.music.feature.tracklist.ui.component.SearchViewTransitionListener26;
import org.singularux.music.feature.tracklist.ui.component.SearchViewTransitionListener34;

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
    public SearchViewTransitionListener providesSearchViewTransitionListener(
            SearchViewOnBackPressedCallback onBackPressedCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return new SearchViewTransitionListener34();
        } else {
            return new SearchViewTransitionListener26(onBackPressedCallback);
        }
    }

}
