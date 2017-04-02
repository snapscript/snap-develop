
package org.snapscript.agent.event;

import org.snapscript.common.thread.ThreadBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessEventExecutor implements Executor {

    private final BlockingQueue<Runnable> tasks;
    private final TaskExecutor executor;
    private final ThreadBuilder builder;
    private final AtomicBoolean active;

    public ProcessEventExecutor() {
        this.tasks = new LinkedBlockingQueue<Runnable>();
        this.executor = new TaskExecutor(1000);
        this.builder = new ThreadBuilder();
        this.active = new AtomicBoolean();
    }

    @Override
    public void execute(Runnable runnable) {
        if(active.compareAndSet(false, true)) {
            Thread thread = builder.newThread(executor);
            thread.start();
        }
        tasks.offer(runnable);
    }

    private class TaskExecutor implements Runnable {

        private final long wait;

        public TaskExecutor(long wait) {
            this.wait = wait;
        }

        @Override
        public void run() {
            try {
                while (active.get()) {
                    Runnable task = tasks.poll(wait, TimeUnit.MILLISECONDS);

                    if (task != null) {
                        try {
                            task.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                active.set(false);
            }
        }
    }
}