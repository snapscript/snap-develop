package org.snapscript.studio.agent.task;

import java.util.concurrent.ThreadFactory;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.agent.DebugContext;
import org.snapscript.studio.agent.ExecuteData;
import org.snapscript.studio.agent.ExecuteLatch;
import org.snapscript.studio.agent.RunMode;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.log.TraceLogger;

public class ProcessExecutor {

   private final ThreadFactory factory;
   private final DebugContext context;
   private final TraceLogger logger;
   private final RunMode mode;
   private final Model model;
   
   public ProcessExecutor(DebugContext context, TraceLogger logger, RunMode mode, Model model) {
      this.factory = new ThreadBuilder();
      this.logger = logger;
      this.context = context;
      this.model = model;
      this.mode = mode;
   }

   public void beginExecute(ProcessEventChannel channel, String project, String resource, String dependencies, boolean debug) {
      ExecuteLatch latch = context.getLatch();
      String process = context.getProcess();
      
      try {         
         if(resource != null) {
            ExecuteData data = new ExecuteData(process, project, resource, dependencies, debug);
            ConsoleConnector connector = new ConsoleConnector(channel, process);
            ProcessTask harness = new ProcessTask(channel, context, mode, model, project, resource, debug);
            
            if(latch.start(data)) {
               Thread thread = factory.newThread(harness);
               ClassLoader loader = ClassPathUpdater.updateClassPath(dependencies);
               
               if(loader == null) {
                  logger.info("Could not update dependencies");
               }
               connector.connect();
               thread.start();
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not execute '" + resource + "' from project '" + project + "'", e);
      }
   }
   
   public void attachProcess(ProcessEventChannel channel, String project, String resource) {
      ExecuteLatch latch = context.getLatch();
      String process = context.getProcess();
      
      try {         
         if(resource != null) {
            ExecuteData data = new ExecuteData(process, project, resource, null, true);
            ProgressReporter reporter = new ProgressReporter(channel, context, project, resource, true);
            ConsoleConnector connector = new ConsoleConnector(channel, process);
            
            if(latch.start(data)) {
               reporter.reportExecuting();
               connector.connect();
            }
         }
      } catch(Exception e) {
         throw new IllegalStateException("Could not execute '" + resource + "' from project '" + project + "'", e);
      }
   }
}