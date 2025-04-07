package com.adithya.aaafexpensemanager.util;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundRunner {
    private final ExecutorService executorService;

    public BackgroundRunner() {
        this.executorService = Executors.newCachedThreadPool(); // Or newSingleThreadExecutor(), newFixedThreadPool(), etc.
    }

    public void runInBackground(Runnable task) {
        executorService.execute(task);
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}