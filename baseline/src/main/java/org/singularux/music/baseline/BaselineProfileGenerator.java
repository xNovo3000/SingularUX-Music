package org.singularux.music.baseline;

import androidx.benchmark.macro.junit4.BaselineProfileRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import kotlin.Unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaselineProfileGenerator {

    @Rule
    public BaselineProfileRule baselineProfileRule = new BaselineProfileRule();

    @Test
    public void generate() {
        baselineProfileRule.collect(
                /* packageName = */ "org.singularux.music",
                /* maxIterations = */ 15,
                /* stableIterations = */ 3,
                /* outputFilePrefix = */ null,
                /* includeInStartupProfile = */ false,
                scope -> {
                    scope.pressHome();
                    scope.startActivityAndWait();
                    // TODO Write more interactions to optimize advanced journeys of your app.
                    return Unit.INSTANCE;
                });
    }
}