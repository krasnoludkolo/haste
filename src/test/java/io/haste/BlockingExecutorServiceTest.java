package io.haste;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockingExecutorServiceTest {

    @Test
    void shouldExecuteRunnable() {
        ExecutorService executor = new BlockingExecutorService();
        IntegerWrapper wrapper = new IntegerWrapper(1000);
        Runnable r = wrapper::increment;

        executor.execute(r);

        assertEquals(1001, wrapper.i.intValue());
    }

    @Test
    void shouldNotExecuteRunnableAfterShutdown() {
        ExecutorService executor = new BlockingExecutorService();
        IntegerWrapper wrapper = new IntegerWrapper(1000);
        Runnable r = wrapper::increment;

        executor.shutdown();
        executor.execute(r);

        assertEquals(1000, wrapper.i.intValue());
    }

    @Test
    void shouldBeTerminatedAfterShutdown() {
        ExecutorService executor = new BlockingExecutorService();

        executor.shutdown();

        assertTrue(executor.isTerminated());
    }

    @Test
    void shouldBeShutdownAfterShutdown() {
        ExecutorService executor = new BlockingExecutorService();

        executor.shutdown();

        assertTrue(executor.isShutdown());
    }

    @Test
    void shouldBeShutdownAfterShutdownNow() {
        ExecutorService executor = new BlockingExecutorService();

        executor.shutdownNow();

        assertTrue(executor.isShutdown());
    }

    class IntegerWrapper {
        Integer i;

        IntegerWrapper(Integer i) {
            this.i = i;
        }

        void increment() {
            i++;
        }
    }

}