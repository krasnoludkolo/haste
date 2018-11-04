package io.haste;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockingExecutorTest {

    @Test
    void shouldExecuteRunnable() {
        Executor executor = new BlockingExecutor();
        Integer i = 1000;
        IntegerWrapper wrapper = new IntegerWrapper(i);
        Runnable r = wrapper::increment;
        executor.execute(r);
        assertEquals(1001, wrapper.i.intValue());
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