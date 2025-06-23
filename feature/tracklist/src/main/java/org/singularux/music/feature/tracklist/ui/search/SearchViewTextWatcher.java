package org.singularux.music.feature.tracklist.ui.search;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import org.singularux.music.feature.tracklist.ui.TrackListViewModel;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class SearchViewTextWatcher implements TextWatcher {

    private final TrackListViewModel viewModel;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
        viewModel.onSearchQueryChanged(s.toString());
    }

}
