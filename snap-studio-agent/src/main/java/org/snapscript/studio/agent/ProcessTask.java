package org.snapscript.studio.agent;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;
import org.snapscript.compile.verify.VerifyError;
import org.snapscript.compile.verify.VerifyException;
import org.snapscript.core.scope.Model;
import org.snapscript.core.trace.Trace;
import org.snapscript.studio.agent.event.BeginEvent;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProfileEvent;
import org.snapscript.studio.agent.event.ScriptErrorEvent;
import org.snapscript.studio.agent.profiler.ProcessProfiler;
import org.snapscript.studio.agent.profiler.ProfileResult;
import org.snapscript.studio.agent.profiler.ProfileResultUpdater;

public class ProcessTask implements Runnable {
   
   private final ProcessEventChannel client;
   private final ProcessContext context;
   private final ProcessMode mode;
   private final String resource;
   private final String project;
   private final Model model;
   private final boolean debug;
   
   public ProcessTask(ProcessEventChannel client, ProcessContext context, ProcessMode mode, Model model, String project, String resource, boolean debug) {
      this.client = client;
      this.resource = resource;
      this.project = project;
      this.context = context;
      this.model = model;
      this.mode = mode;
      this.debug = debug;
   }
   
   @Override
   public void run() {
      CountDownLatch latch = context.getLatch();
      String process = context.getProcess();
      
      // start and listen for the socket close
      long start = System.nanoTime();
      try {
         ProcessProfiler profiler = context.getProfiler();
         ResourceCompiler compiler = context.getCompiler();
         Executable executable = compiler.compile(resource);
         long middle = System.nanoTime();
         ProfileResultUpdater updater = new ProfileResultUpdater(profiler, client);
         BeginEvent event = new BeginEvent.Builder(process)
            .withProject(project)
            .withResource(resource)
            .withMode(context.getMode())
            .withDuration(TimeUnit.NANOSECONDS.toMillis(middle-start))
            .withDebug(debug)
            .build();
         
         client.send(event);
         
         try {
            updater.start(process); // start sending profile events!!!
            middle = System.nanoTime();
            executable.execute(model); // execute the script
         } catch(VerifyException e) {
            List<VerifyError> errors = e.getErrors();
            
            for(VerifyError error : errors) {
               Exception cause = error.getCause();
               Trace trace = error.getTrace();
               String description = cause.getMessage();
               String path = trace.getPath().toString();
               int line = trace.getLine();
               ScriptErrorEvent message = new ScriptErrorEvent.Builder(process)
                  .withDescription(description)
                  .withResource(path)
                  .withLine(line)
                  .build();
               
               client.send(message);
            }
         } catch(Throwable e) {
            e.printStackTrace();
         }finally {
            try {
               long stop = System.nanoTime();
               System.err.flush(); // flush output to sockets
               System.out.flush();
               Thread.sleep(200);
               // should really be a heat map for the editor
               SortedSet<ProfileResult> lines = profiler.lines(200);
               System.err.flush();
               System.out.flush();
               
               ProfileEvent profileEvent = new ProfileEvent.Builder(process)
                  .withResults(lines)
                  .build();
               
               client.send(profileEvent);
               Thread.sleep(2000);
               System.err.close();
               System.out.close();
               
               ExitEvent exitEvent = new ExitEvent.Builder(process)
                  .withDuration(TimeUnit.NANOSECONDS.toMillis(stop-middle))
                  .withMode(context.getMode())
                  .build();
               
               client.send(exitEvent);
            } catch(Exception e) {
               e.printStackTrace();
            } finally {
               if(mode.isTerminateRequired()) {
                  ProcessTerminator.terminate("Task has finished executing"); // shutdown when finished
               }
               latch.countDown();
            }
         }
      } catch (Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      } finally {
         if(mode.isTerminateRequired()) {
            ProcessTerminator.terminate("Task has finished executing"); // shutdown when finished
         }
         latch.countDown();
      }
   }
}