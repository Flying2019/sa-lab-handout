/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MonitorTest {

    @Test
    void runAndCountStopsMonitorWhenTaskThrows() {
        String taskName = "MonitorTest.runAndCountStopsMonitorWhenTaskThrows";
        IllegalStateException exception = new IllegalStateException();

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                Monitor.runAndCount(() -> {
                    // Ensure the sampler thread exists before failing the task.
                    assertTrue(waitUntilMonitorThreadStarts(taskName));
                    throw exception;
                }, taskName));

        assertSame(exception, thrown);
        assertTrue(waitUntilMonitorThreadStops(taskName),
                "Monitor should stop its sampler thread when the task throws");
    }

    @Test
    void runWithTimeoutThrowsWhenTaskTimesOut() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                Monitor.runWithTimeout(taskThatTimesOut(), 0));

        assertTrue(exception.getMessage().contains("timed out"));
    }

    private static Runnable taskThatTimesOut() {
        return () -> LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
    }

    private static boolean waitUntilMonitorThreadStarts(String taskName) {
        return waitUntil(() -> monitorThreads(taskName).anyMatch(Thread::isAlive));
    }

    private static boolean waitUntilMonitorThreadStops(String taskName) {
        return waitUntil(() -> monitorThreads(taskName).noneMatch(Thread::isAlive));
    }

    private static boolean waitUntil(BooleanSupplier condition) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            sleep();
        }
        return false;
    }

    private static Stream<Thread> monitorThreads(String taskName) {
        String threadName = Monitor.class.getName() + "[" + taskName + "]";
        return Thread.getAllStackTraces()
                .keySet()
                .stream()
                .filter(thread -> thread.getName().equals(threadName));
    }

    private static void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
        }
    }
}
