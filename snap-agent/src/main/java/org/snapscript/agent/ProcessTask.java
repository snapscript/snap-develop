package org.snapscript.agent;

import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.profiler.ProcessProfiler;
import org.snapscript.agent.profiler.ProfileResult;
import org.snapscript.agent.profiler.ProfileResultUpdater;
import org.snapscript.compile.Executable;
import org.snapscript.compile.ResourceCompiler;

public class ProcessTask implements Runnable {
   
   private final ProcessEventChannel client;
   private final ProcessContext context;
   private final String resource;
   private final String project;
   
   public ProcessTask(ProcessEventChannel client, ProcessContext context, String project, String resource) {
      this.client = client;
      this.resource = resource;
      this.project = project;
      this.context = context;
   }
   
   @Override
   public void run() {
      String process = context.getProcess();
      ConsoleConnector.connect(client, process);
      
      // start and listen for the socket close
      long start = System.nanoTime();
      try {
         ProcessProfiler profiler = context.getProfiler();
         ResourceCompiler compiler = context.getCompiler();
         Executable executable = compiler.compile(resource);
         long middle = System.nanoTime();
         BeginEvent event = new BeginEvent(process, project, resource, TimeUnit.NANOSECONDS.toMillis(middle-start));
         ProfileResultUpdater updater = new ProfileResultUpdater(profiler, client);
         client.send(event);
         
         try {
            updater.start(process); // start sending profile events!!!
            middle = System.nanoTime();
            executable.execute(); // execute the script
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
               ProfileEvent profileEvent = new ProfileEvent(process, lines);
               client.send(profileEvent);
               Thread.sleep(2000);
               System.err.close();
               System.out.close();
               ExitEvent exitEvent = new ExitEvent(process, TimeUnit.NANOSECONDS.toMillis(stop-middle));
               client.send(exitEvent);
            } catch(Exception e) {
               e.printStackTrace();
            } finally {
               System.exit(0); // shutdown when finished  
            }
         }
      } catch (Exception e) {
         System.err.println(ExceptionBuilder.build(e));
      } finally {
         System.exit(0); // shutdown when finished  
      }
   }
}
