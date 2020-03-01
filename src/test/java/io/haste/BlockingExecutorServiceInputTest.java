package io.haste;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockingExecutorServiceInputTest {

    @Test
    void shouldThrowExceptionWhenPassingNegativeTimeUnit() {
        var executor = new BlockingExecutorService();
        assertThrows(
                IllegalArgumentException.class,
                () -> executor.awaitTermination(-1, TimeUnit.MINUTES)
        );
    }

    @Test
    void shouldThrowExceptionWhenPassingNullTimeUnit() {
        var executor = new BlockingExecutorService();
        assertThrows(
                NullPointerException.class,
                () -> executor.awaitTermination(1, null)
        );
    }
}