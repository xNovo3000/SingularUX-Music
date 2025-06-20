package org.singularux.music.feature.tracklist.ui.observer;

import androidx.activity.OnBackPressedCallback;

import com.google.android.material.search.SearchView;

import javax.annotation.Nullable;

import lombok.Setter;

@Setter
public class SearchViewOnBackPressedCallback extends OnBackPressedCallback {

    private @Nullable SearchView searchView = null;

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
