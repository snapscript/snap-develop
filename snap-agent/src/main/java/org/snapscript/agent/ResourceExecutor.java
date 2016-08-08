package org.snapscript.agent;

import java.util.concurrent.ThreadFactory;

import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.common.ThreadBuilder;

public class ResourceExecutor {

   private final ProcessContext context;
   private final ThreadFactory factory;
   
   public ResourceExecutor(ProcessContext context) throws Exception {
      this.factory = new ThreadBuilder();
      this.context = context;
   }

   public void execute(ProcessEventChannel channel, String project, String resource) throws Exception {
      ProcessTask task = new ProcessTask(channel, context, project, resource);
      
      if(resource != null) {
         Thread thread = factory.newThread(task);
         thread.start();
      }
   }
}
