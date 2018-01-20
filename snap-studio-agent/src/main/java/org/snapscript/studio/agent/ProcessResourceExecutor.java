package org.snapscript.studio.agent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.core.Model;
import org.snapscript.studio.agent.event.ExecuteData;
import org.snapscript.studio.agent.event.ProcessEventChannel;

public class ProcessResourceExecutor {

   private final AtomicReference<ExecuteData> reference;
   private final ProcessContext context;
   private final ProcessMode mode;
   private final ThreadFactory factory;
   private final Model model;
   
   public ProcessResourceExecutor(ProcessContext context, ProcessMode mode, Model model) {
      this.reference = new AtomicReference<ExecuteData>();
      this.factory = new ThreadBuilder();
      this.context = context;
      this.model = model;
      this.mode = mode;
   }
   
   public ExecuteData get() {
      return reference.get();
   }

   public void execute(ProcessEventChannel channel, String process, String project, String resource, String dependencies, boolean debug) {
      try {
         ExecuteData data = new ExecuteData(process, project, resource, dependencies, debug);
         ConsoleConnector connector = new ConsoleConnector(channel, process);
         ProcessTask task = new ProcessTask(channel, context, mode, model, project, resource, debug);
         
         if(resource != null) {
            Thread thread = factory.newThread(task);
            
            if(reference.compareAndSet(null, data)) {
               ClassPathUpdater.updateClassPath(dependencies);
               connector.connect();
               thread.start();
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not execute '" + resource + "' from project '" + project + "'", e);
      }
   }
}