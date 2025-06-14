package org.singularux.music.feature.tracklist.ui.component;

import androidx.activity.OnBackPressedCallback;

import com.google.android.material.search.SearchView;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.hilt.android.scopes.FragmentScoped;
import lombok.Setter;

@FragmentScoped
@Setter
public class SearchViewOnBackPressedCallback extends OnBackPressedCallback {

    private @Nullable SearchView searchView = null;

    @Inject
    public SearchViewOnBackPressedCallback() {
        super(false);
    }

    @Override
    public void handleOnBackPressed() {
        if (searchView != null) {
            searchView.hide();
        }
    }

}
