package org.snapscript.develop;

import java.util.Map;
import java.util.Set;

import org.snapscript.agent.ConsoleLogger;
import org.snapscript.agent.event.BreakpointsEvent;
import org.snapscript.agent.event.BrowseEvent;
import org.snapscript.agent.event.ExecuteEvent;
import org.snapscript.agent.event.PingEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.StepEvent;

public class ProcessConnection {

   private final ProcessEventChannel channel;
   private final ConsoleLogger logger;
   private final String process;

   public ProcessConnection(ProcessEventChannel channel, ConsoleLogger logger, String process) {
      this.channel = channel;
      this.process = process;
      this.logger = logger;
   }

   public boolean execute(String project, String path, Map<String, Map<Integer, Boolean>> breakpoints) {
      try {
         ExecuteEvent event = new ExecuteEvent.Builder(process)
            .withProject(project)
            .withResource(path)
            .withBreakpoints(breakpoints)
            .build();

         return channel.send(event);
      } catch (Exception e) {
         logger.log(process + ": Error occured sending execute event", e);
         close();
         throw new IllegalStateException("Could not execute script '" + path + "' for '" + process + "'", e);
      }
   }
   
   public boolean suspend(Map<String, Map<Integer, Boolean>> breakpoints) {
      try {
         BreakpointsEvent event = new BreakpointsEvent.Builder(process)
            .withBreakpoints(breakpoints)
            .build();
         
         return channel.send(event);
      } catch (Exception e) {
         logger.log(process + ": Error occured sending suspend event", e);
         close();
         throw new IllegalStateException("Could not set breakpoints '" + breakpoints + "' for '" + process + "'", e);
      }
   }
   
   public boolean browse(String thread, Set<String> expand) {
      try {
         BrowseEvent event = new BrowseEvent.Builder(process)
            .withThread(thread)
            .withExpand(expand)
            .build();
         
         return channel.send(event);
      } catch (Exception e) {
         logger.log(process + ": Error occured sending browse event", e);
         close();
         throw new IllegalStateException("Could not browse '" + thread + "' for '" + process + "'", e);
      }
   }
   
   public boolean step(String thread, int type) {
      try {
         StepEvent event = new StepEvent.Builder(process)
            .withThread(thread)
            .withType(type)
            .build();

         return channel.send(event);
      } catch (Exception e) {
         logger.log(process + ": Error occured sending step event", e);
         close();
         throw new IllegalStateException("Could not resume script thread '" + thread + "' for '" + process + "'", e);
      }
   }

   public boolean ping(long time) {
      try {
         PingEvent event = new PingEvent.Builder(process)
            .withTime(time)
            .build();
         
         if(channel.send(event)) {
            logger.debug(process + ": Ping succeeded");
            return true;
         }
         logger.log(process + ": Ping failed");
      } catch (Exception e) {
         logger.log(process + ": Error occured sending ping event", e);
         close();
      }
      return false;
   }
   
   public void close() {
      try {
         logger.log(process + ": Closing connection");
         channel.close();
      } catch (Exception e) {
         logger.log(process + ": Error occured closing channel", e);
      }
   }
   
   @Override
   public String toString() {
      return process;
   }
}
