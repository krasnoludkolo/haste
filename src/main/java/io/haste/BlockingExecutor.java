package io.haste;

import java.util.concurrent.Executor;

class BlockingExecutor implements Executor {

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

}
