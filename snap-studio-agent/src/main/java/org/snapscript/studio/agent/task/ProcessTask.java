package org.snapscript.studio.agent.task;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.verify.VerifyError;
import org.snapscript.compile.verify.VerifyException;
import org.snapscript.core.scope.Model;
import org.snapscript.studio.agent.DebugContext;
import org.snapscript.studio.agent.RunMode;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.profiler.TraceProfiler;
import org.snapscript.studio.agent.profiler.ProfileResultUpdater;

public class ProcessTask implements Runnable {
   
   private final ProgressReporter reporter;
   private final ProcessEventChannel client;
   private final DebugContext context;
   private final String resource;
   private final Model model;
   
   public ProcessTask(ProcessEventChannel client, DebugContext context, RunMode mode, Model model, String project, String resource, boolean debug) {
      this.reporter = new ProgressReporter(client, context, project, resource, debug);
      this.client = client;
      this.resource = resource;
      this.context = context;
      this.model = model;
   }
   
   @Override
   public void run() {
      TraceProfiler profiler = context.getProfiler();
      ResourceCompiler compiler = context.getCompiler();
      String process = context.getProcess();
      long start = System.nanoTime();
      
      try {         
         ProfileResultUpdater updater = new ProfileResultUpdater(profiler, client);
         
         reporter.reportCompiling();         
         Executable executable = compiler.compile(resource);
         reporter.reportExecuting();
         
         try {
            updater.start(process); // start sending profile events!!
            executable.execute(model); // execute the script
         } catch(VerifyException e) {
            List<VerifyError> errors = e.getErrors();
            reporter.reportError(errors);
         } catch(Throwable cause) {
            ConsoleFlusher.flushError(cause);
         }finally {            
            try {
               reporter.reportTerminating();
               reporter.reportProfile(); // one last update
               ConsoleFlusher.flush();               
            } catch(Exception cause) {
               ConsoleFlusher.flushError(cause);
            } 
         }
      } catch (Exception cause) {
         ConsoleFlusher.flushError(cause);
      } finally {
         long finish = System.nanoTime();
         long duration = TimeUnit.NANOSECONDS.toMillis(finish - start);
         
         reporter.reportFinished(duration);
      }
   }
}