/*
 * ProcessEventExecutor.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.agent.event;

import org.snapscript.common.ThreadBuilder;

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