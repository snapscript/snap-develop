package org.snapscript.studio.configuration;

import java.util.concurrent.Executor;

import org.snapscript.common.thread.ThreadPool;
import org.snapscript.studio.resource.project.Project;

public class ClassPathExecutor implements Executor {

   private final Project project;
   private final ThreadPool pool;
   
   public ClassPathExecutor(ThreadPool pool, Project project) {
      this.project = project;
      this.pool = pool;
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
         ClassLoader context = project.getClassLoader();
         thread.setContextClassLoader(context);
         task.run();
      }
   }
   
}