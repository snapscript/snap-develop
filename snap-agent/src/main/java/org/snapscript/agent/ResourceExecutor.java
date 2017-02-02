package org.snapscript.agent;

import java.util.concurrent.ThreadFactory;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.common.ThreadBuilder;
import org.snapscript.core.Model;

public class ResourceExecutor {

   private final ProcessContext context;
   private final ProcessMode mode;
   private final ThreadFactory factory;
   private final Model model;
   
   public ResourceExecutor(ProcessContext context, ProcessMode mode, Model model) throws Exception {
      this.factory = new ThreadBuilder();
      this.context = context;
      this.model = model;
      this.mode = mode;
   }

   public void execute(ProcessEventChannel channel, String process, String project, String resource) throws Exception {
      ConsoleConnector connector = new ConsoleConnector(channel, process);
      ProcessTask task = new ProcessTask(channel, context, mode, model, project, resource);
      
      if(resource != null) {
         Thread thread = factory.newThread(task);
         
         connector.connect();
         thread.start();
      }
   }
}
