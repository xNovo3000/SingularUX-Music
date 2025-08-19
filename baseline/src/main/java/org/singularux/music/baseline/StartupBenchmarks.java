package org.singularux.music.baseline;

import androidx.benchmark.macro.BaselineProfileMode;
import androidx.benchmark.macro.CompilationMode;
import androidx.benchmark.macro.StartupMode;
import androidx.benchmark.macro.StartupTimingMetric;
import androidx.benchmark.macro.junit4.MacrobenchmarkRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import kotlin.Unit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StartupBenchmarks {

    @Rule
    public MacrobenchmarkRule rule = new MacrobenchmarkRule();

    @Test
    public void startupCompilationNone() {
        benchmark(new CompilationMode.None());
    }

    @Test
    public void startupCompilationBaselineProfiles() {
        benchmark(new CompilationMode.Partial(BaselineProfileMode.Require));
    }

    private void benchmark(CompilationMode compilationMode) {
        rule.measureRepeated(
                "org.singularux.music",
                Collections.singletonList(new StartupTimingMetric()),
                compilationMode,
                StartupMode.COLD,
                10,
                setupScope -> {
                    setupScope.pressHome();
                    return Unit.INSTANCE;
                },
                measureScope -> {
                    measureScope.startActivityAndWait();
                    return Unit.INSTANCE;
                }
        );
    }
}