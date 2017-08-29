package org.zalando.switchman.repo;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;

import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;

import rx.schedulers.TestScheduler;

public final class RxJavaSchedulerUtils {

    private static final TestScheduler TEST_SCHEDULER = new TestScheduler();
    private static final RxJavaSchedulersHook TEST_SCHEDULER_HOOK = new SchedulerHook(TEST_SCHEDULER);

    private RxJavaSchedulerUtils() { }

    /**
     * This method overrides io and computation schedulers with the test scheduler so subscribing and observing the
     * Observable will execute on immediate scheduler i.e on the current thread.
     *
     * <p>
     * <p><b>Example:</b> In your test class write
     *
     * <p>
     * <pre>
       static{
           RxJavaUtils.overrideSchedulersWithTestScheduler()
       }
     * </pre>
     * </p>
     *
     * @see  RxJavaPlugins#registerSchedulersHook(RxJavaSchedulersHook)
     */
    public static void overrideSchedulersWithTestScheduler() {
        RxJavaPlugins.getInstance().reset();
        RxJavaPlugins.getInstance().registerSchedulersHook(TEST_SCHEDULER_HOOK);
    }

    /**
     * Advances Scheduler's clock time by given amount of seconds.
     *
     * @param  amount  the amount of seconds to advance by
     *
     * @see    RxJavaSchedulerUtils#overrideSchedulersWithTestScheduler()
     */
    public static void advanceInSeconds(final long amount) {
        advanceTimeBy(amount, TimeUnit.SECONDS);
    }

    public static void advanceOneSecond() {
        advanceInSeconds(1);
    }

    /**
     * Advances the Scheduler's clock time forward by a specified amount of time.
     *
     * @param  delayTime  the point in time to move the Scheduler's clock to
     * @param  unit       the units of time that {@code delayTime} is expressed in
     *
     * @see    RxJavaSchedulerUtils#overrideSchedulersWithTestScheduler()
     */
    public static void advanceTimeBy(final long delayTime, final TimeUnit unit) {
        TEST_SCHEDULER.advanceTimeBy(delayTime, unit);
    }

    private static final class SchedulerHook extends RxJavaSchedulersHook {
        private final Scheduler scheduler;

        private SchedulerHook(final Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public Scheduler getNewThreadScheduler() {
            return scheduler;
        }

        @Override
        public Scheduler getIOScheduler() {
            return scheduler;
        }

        @Override
        public Scheduler getComputationScheduler() {
            return scheduler;
        }

        @Override
        public String toString() {
            return "RxJavaSchedulersHook, Scheduler: " + scheduler;
        }
    }

}
