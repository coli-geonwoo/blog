package com.example.blog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SaturationPolicyTest {

    private ExecutorService executor;

    @AfterEach
    void tearDown() {
        executor.shutdown();
    }

    @DisplayName("AbortPolicy : RejectedExecutionException 발생 ")
    @Test
    void testAbortPolicy() {
        executor = new ThreadPoolExecutor(
                1, // corePoolSize
                1, // maximumPoolSize
                0L, // keepAliveTime
                TimeUnit.MILLISECONDS, // timeUnit
                new ArrayBlockingQueue<>(1), // workQueue
                new ThreadPoolExecutor.AbortPolicy() // AbortPolicy
        );

        List<CallableEx> callables = List.of(new CallableEx(1), new CallableEx(2), new CallableEx(3));

        assertThatThrownBy(() -> executor.invokeAll(callables))
                .isInstanceOf(RejectedExecutionException.class);
    }

    @DisplayName("CallersRunPolicy : 요청한 스레드에서 작업을 시행한다.")
    @Test
    void testCallersRunPolicy() throws InterruptedException {
        String requestThreadName = Thread.currentThread().getName();

        executor = new ThreadPoolExecutor(
                1, // corePoolSize
                1, // maximumPoolSize
                0L, // keepAliveTime
                TimeUnit.MILLISECONDS, // timeUnit
                new ArrayBlockingQueue<>(1), // workQueue
                new ThreadPoolExecutor.CallerRunsPolicy() // CallersRunPolicy
        );

        List<CallableEx> callables = List.of(new CallableEx(1), new CallableEx(2), new CallableEx(3));

        List<Future<String>> results = executor.invokeAll(callables, 1000, TimeUnit.MILLISECONDS);
        List<String> threadNames = results.stream()
                .map(SaturationPolicyTest::getResult)
                .toList();

        assertThat(threadNames).contains(requestThreadName);
    }

    @DisplayName("DiscardPolicy : 포화 상태에서 요청한 작업을 버린다 == 시행하지 않는다.")
    @Test
    void testCallersDiscardPolicy() throws InterruptedException {

        executor = new ThreadPoolExecutor(
                1, // corePoolSize
                1, // maximumPoolSize
                0L, // keepAliveTime
                TimeUnit.MILLISECONDS, // timeUnit
                new ArrayBlockingQueue<>(1), // workQueue
                new ThreadPoolExecutor.DiscardPolicy() // DiscardPolicy
        );

        List<CallableEx> callables = List.of(new CallableEx(1), new CallableEx(2), new CallableEx(3));

        List<Future<String>> results = executor.invokeAll(callables, 1000, TimeUnit.MILLISECONDS);
        List<Boolean> cancelled = results.stream()
                .map(Future::isCancelled)
                .toList();

        assertThat(cancelled).contains(true);
    }

    @DisplayName("DiscardOldestPolicy : 가장 오래 대기한 요청을 버리고 재시도한다.")
    @Test
    void testDiscardOldestPolicy() throws InterruptedException {
        executor = new ThreadPoolExecutor(
                1, // corePoolSize
                1, // maximumPoolSize
                0L, // keepAliveTime
                TimeUnit.MILLISECONDS, // timeUnit
                new ArrayBlockingQueue<>(2), // workQueue
                new ThreadPoolExecutor.DiscardOldestPolicy()// DiscardPolicy
        );

        List<CallableEx> callables = List.of(
                new CallableEx(1),
                new CallableEx(2),
                new CallableEx(3),
                new CallableEx(4)
        );
        /*
        core(size 1) | queue(size 2) |
        1            |               | 2 3 4
        1            |  2            | 3 4
        1            |  2 3          | 4
        1            |  2(취소) 3     | 4
        1            |  3 4          |
        3            |  4            |
        4            |               |
         */

        List<Future<String>> results = executor.invokeAll(callables, 1000, TimeUnit.MILLISECONDS);
        List<Boolean> cancelled = results.stream()
                .map(Future::isCancelled)
                .toList();

        assertThat(cancelled).containsExactly(false, true, false, false);
    }

    static class CallableEx implements Callable<String> {
        private final int num;

        public CallableEx(int num) {
            this.num = num;
        }

        @Override
        public String call() {
            System.out.println("Thread Name : " + Thread.currentThread().getName() + " / num : " + num);
            return Thread.currentThread().getName();
        }
    }

    private static String getResult(Future<String> future) {
        try {
            return future.get(1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
