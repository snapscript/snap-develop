package org.snapscript.studio.configuration;

import java.util.concurrent.Executor;

import org.snapscript.studio.resource.project.Project;

public class ClassPathExecutor implements Executor {

   private final Executor executor;
   private final Project project;
   
   public ClassPathExecutor(Project project, Executor executor) {
      this.executor = executor;
      this.project = project;
   }
   
   @Override
   public void execute(Runnable command) {
      CompletionTask task = new CompletionTask(command);
      executor.execute(task);
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