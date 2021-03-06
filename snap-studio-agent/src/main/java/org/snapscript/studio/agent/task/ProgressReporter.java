package org.snapscript.studio.agent.task;

import static org.snapscript.studio.agent.core.ExecuteStatus.COMPILING;
import static org.snapscript.studio.agent.core.ExecuteStatus.DEBUGGING;
import static org.snapscript.studio.agent.core.ExecuteStatus.FINISHED;
import static org.snapscript.studio.agent.core.ExecuteStatus.RUNNING;
import static org.snapscript.studio.agent.core.ExecuteStatus.TERMINATING;

import java.util.List;
import java.util.SortedSet;

import org.snapscript.compile.verify.VerifyError;
import org.snapscript.core.trace.Trace;
import org.snapscript.studio.agent.ProcessContext;
import org.snapscript.studio.agent.ProcessMode;
import org.snapscript.studio.agent.core.ExecuteData;
import org.snapscript.studio.agent.core.ExecuteLatch;
import org.snapscript.studio.agent.core.ExecuteState;
import org.snapscript.studio.agent.core.TerminateHandler;
import org.snapscript.studio.agent.event.BeginEvent;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProfileEvent;
import org.snapscript.studio.agent.event.ProgressEvent;
import org.snapscript.studio.agent.event.ScriptErrorEvent;
import org.snapscript.studio.agent.profiler.TraceProfiler;
import org.snapscript.studio.agent.profiler.ProfileResult;

public class ProgressReporter {
   
   private final ProcessEventChannel client;
   private final ProcessContext context;
   private final String project;
   private final String resource;
   private final boolean debug;
   
   public ProgressReporter(ProcessContext context, ProcessEventChannel client, String project, String resource, boolean debug) {
      this.context = context;   
      this.client = client;
      this.project = project;
      this.resource = resource;
      this.debug = debug;
   }  
   
   public void reportCompiling() {
      ExecuteLatch latch = context.getLatch();    
      ExecuteState state = latch.getState();
      ExecuteData data = state.getData();
      String process = state.getProcess();
      String system = state.getSystem();
      String project = data.getProject();
      String pid = state.getPid();
      long duration = latch.update(COMPILING);
      
      if(duration >= 0) {
         try {  
            ProgressEvent event = new ProgressEvent.Builder(process)
               .withPid(pid)
               .withSystem(system)
               .withProject(project)
               .withResource(resource)
               .withStatus(COMPILING)
               .withTotalMemory(Runtime.getRuntime().totalMemory())
               .withUsedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
               .withThreads(Thread.getAllStackTraces().size()) // this might be expensive
               .build();
                
            client.send(event);
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }
   
   public void reportExecuting() {
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      ExecuteData data = state.getData();
      String process = state.getProcess();
      String system = state.getSystem();
      String project = data.getProject();
      String pid = state.getPid();
      long duration = latch.update(debug ? DEBUGGING : RUNNING);
      
      if(duration >= 0) {
         try {
            BeginEvent event = new BeginEvent.Builder(process)
               .withMode(context.getMode())
               .withDuration(duration)
               .withPid(pid)
               .withSystem(system)
               .withProject(project)
               .withResource(resource)
               .withStatus(debug ? DEBUGGING : RUNNING)
               .withTotalMemory(Runtime.getRuntime().totalMemory())
               .withUsedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
               .withThreads(Thread.getAllStackTraces().size()) // this might be expensive
               .build();
            
            client.send(event);
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }   
   
   public void reportError(List<VerifyError> errors) {
      String process = context.getProcess();   
      
      for(VerifyError error : errors) {
         Exception cause = error.getCause();
         Trace trace = error.getTrace();
         String description = cause.getMessage();
         String path = trace.getPath().toString();
         int line = trace.getLine();
         
         try {
            ScriptErrorEvent event = new ScriptErrorEvent.Builder(process)
               .withDescription(description)
               .withResource(path)
               .withLine(line)
               .build();
            
            client.send(event);
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }
   
   public void reportTerminating() {   
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      String process = state.getProcess();
      String system = state.getSystem();
      String pid = state.getPid();
      long duration = latch.update(TERMINATING);
      
      if(duration >= 0) {
         try {  
            ProgressEvent event = new ProgressEvent.Builder(process)
               .withPid(pid)
               .withSystem(system)
               .withProject(project)
               .withResource(resource)
               .withStatus(TERMINATING)
               .withTotalMemory(Runtime.getRuntime().totalMemory())
               .withUsedMemory(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())
               .withThreads(Thread.getAllStackTraces().size()) // this might be expensive
               .build();
               
   
            client.send(event);       
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
   }
   
   public void reportProfile() {   
      ExecuteLatch latch = context.getLatch();
      ExecuteState state = latch.getState();
      TraceProfiler profiler = context.getProfiler();
      SortedSet<ProfileResult> lines = profiler.lines(200);
      String process = state.getProcess();
      
      try {  
         ProfileEvent profile = new ProfileEvent.Builder(process)
            .withResults(lines)
            .build();
            
         client.send(profile);       
      } catch(Exception e) {
         ConsoleFlusher.flushError(e);
      }
   }
   
   public void reportFinished(long totalTime) {
      ProcessMode mode = context.getMode();
      ExecuteLatch latch = context.getLatch();  
      String process = context.getProcess();  
      long duration = latch.update(FINISHED);
      
      if(duration >= 0) {
         try { 
            ExitEvent event = new ExitEvent.Builder(process)
               .withDuration(totalTime)
               .withMode(context.getMode())
               .build();         
   
   
            client.send(event);
         } catch(Exception e) {
            ConsoleFlusher.flushError(e);
         }
      }
      if(mode.isTerminateRequired()) {
         TerminateHandler.terminate("Task has finished executing"); // shutdown when finished
      }
      
   }
}
