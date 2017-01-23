package org.snapscript.agent;

import java.util.concurrent.ThreadFactory;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.common.ThreadBuilder;
import org.snapscript.core.Model;

public class ResourceExecutor {

   private final ProcessContext context;
   private final ThreadFactory factory;
   private final Model model;
   
   public ResourceExecutor(ProcessContext context, Model model) throws Exception {
      this.factory = new ThreadBuilder();
      this.context = context;
      this.model = model;
   }

   public void execute(ProcessEventChannel channel, String project, String resource) throws Exception {
      ProcessTask task = new ProcessTask(channel, context, model, project, resource);
      
      if(resource != null) {
         Thread thread = factory.newThread(task);
         thread.start();
      }
   }
}
