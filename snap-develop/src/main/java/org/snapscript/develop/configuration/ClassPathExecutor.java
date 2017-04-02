
package org.snapscript.develop.configuration;

import java.util.concurrent.Executor;

import org.snapscript.common.thread.ThreadPool;

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
