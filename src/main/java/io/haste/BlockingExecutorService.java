package io.haste;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

class BlockingExecutorService extends AbstractExecutorService implements ExecutorService {

    private boolean shutdown = false;

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutdown = true;
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return isShutdown();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit);
        if (l < 0) throw new IllegalArgumentException();
        shutdown();
        return true;
    }

    @Override
    public void execute(Runnable runnable) {
        if (!shutdown) {
            runnable.run();
        }
    }
}
