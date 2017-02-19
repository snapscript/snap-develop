/*
 * ClassPathExecutor.java December 2016
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

package org.snapscript.develop.configuration;

import java.util.concurrent.Executor;

import org.snapscript.common.ThreadPool;

public class ClassPathExecutor implements Executor {

   private final ConfigurationClassLoader loader;
   private final ThreadPool pool;
   
   public ClassPathExecutor(ConfigurationClassLoader loader) {
      this(loader, 6);
   }
   
   public ClassPathExecutor(ConfigurationClassLoader loader, int threads) {
      this.pool = new ThreadPool(threads);
      this.loader = loader;
   }
   
   @Override
   public void execute(Runnable command) {
      CompletionTask task = new CompletionTask(command);
      pool.execute(task);
   }

   private class CompletionTask implements Runnable {
      
      private final Runnable task;
      
      public CompletionTask(Runnable task) {
         this.task = task;
      }
      
      @Override
      public void run() {
         Thread thread = Thread.currentThread();
         ClassLoader context = loader.getClassLoader();
         thread.setContextClassLoader(context);
         task.run();
      }
   }
   
}
