package com.tiensinoakuma.tiensinowashroom.util;

/**
 * Created by tiensi on 7/21/18.
 */

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.TestScheduler;

public class TestSchedulerRule implements TestRule {
    public final TestScheduler scheduler = new TestScheduler();

    private final Function<Callable<Scheduler>, Scheduler> initHandler = new Function<Callable<Scheduler>, Scheduler>() {
        @Override
        public Scheduler apply(@NonNull Callable<Scheduler> scheduler) throws Exception {
            return TestSchedulerRule.this.scheduler;
        }
    };

    private final Function<Scheduler, Scheduler> handler = new Function<Scheduler, Scheduler>() {
        @Override
        public Scheduler apply(@NonNull Scheduler scheduler) throws Exception {
            return TestSchedulerRule.this.scheduler;
        }
    };

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                RxJavaPlugins.setInitIoSchedulerHandler(initHandler);
                RxJavaPlugins.setInitComputationSchedulerHandler(initHandler);
                RxJavaPlugins.setInitNewThreadSchedulerHandler(initHandler);
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(initHandler);
                RxJavaPlugins.setIoSchedulerHandler(handler);
                RxJavaPlugins.setComputationSchedulerHandler(handler);
                RxJavaPlugins.setNewThreadSchedulerHandler(handler);
                RxAndroidPlugins.setMainThreadSchedulerHandler(handler);

                try {
                    base.evaluate();
                } finally {
                    RxJavaPlugins.reset();
                    RxAndroidPlugins.reset();
                }
            }
        };
    }
}
