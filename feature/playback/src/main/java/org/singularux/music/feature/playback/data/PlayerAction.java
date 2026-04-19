package org.singularux.music.feature.playback.data;

import java.time.Duration;

import lombok.EqualsAndHashCode;
import lombok.Value;

public class PlayerAction {

    public static class Play extends PlayerAction {}
    public static class Pause extends PlayerAction {}
    public static class SkipPrev extends PlayerAction {}
    public static class SkipNext extends PlayerAction {}

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class SeekTo extends PlayerAction {
        Duration duration;
    }

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class AddToQueue extends PlayerAction {
        // TODO
    }

}
