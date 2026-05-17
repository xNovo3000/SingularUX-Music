package org.singularux.music.feature.playback.data;

import androidx.annotation.NonNull;

import java.time.Duration;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

public class PlayerAction {

    @ToString public static final class Play extends PlayerAction {}
    @ToString public static final class Pause extends PlayerAction {}
    @ToString public static final class SkipPrev extends PlayerAction {}
    @ToString public static final class SkipNext extends PlayerAction {}

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class SeekTo extends PlayerAction {
        @NonNull Duration duration;
    }

}
