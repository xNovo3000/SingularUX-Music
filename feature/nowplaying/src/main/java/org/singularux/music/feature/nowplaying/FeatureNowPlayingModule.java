package org.singularux.music.feature.nowplaying;

import android.content.Context;

import androidx.core.content.ContextCompat;

import org.singularux.music.feature.nowplaying.ui.utils.SliderDurationLabelFormatter;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.FragmentScoped;

@Module
@InstallIn(FragmentComponent.class)
public class FeatureNowPlayingModule {

    @Provides
    @FragmentScoped
    public SliderDurationLabelFormatter providesSliderDurationLabelFormatter(
            @ActivityContext Context context
    ) {
        return new SliderDurationLabelFormatter(ContextCompat.getContextForLanguage(context));
    }

}
