package org.singularux.music.feature.tracklist.ui.observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.singularux.music.feature.tracklist.ui.TrackListViewModel;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ActivityRetainedScoped;
import dagger.hilt.android.scopes.FragmentScoped;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@ActivityRetainedScoped
public class SearchViewTextChangedListener implements TextWatcher, FlowableOnSubscribe<String> {

    private static final String TAG = "SearchViewTextChangedListener";

    private @Nullable FlowableEmitter<String> emitter = null;

    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
        Log.d(TAG, "Received: " + s);
        if (emitter != null) {
            Log.d(TAG, "Emitting: " + s);
            emitter.onNext(s.toString());
        }
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<String> emitter) {
        this.emitter = emitter;
    }

}
