package org.snapscript.agent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.agent.debug.BreakpointMatcher;
import org.snapscript.agent.debug.ResumeType;
import org.snapscript.agent.debug.SuspendController;
import org.snapscript.agent.event.BreakpointsEvent;
import org.snapscript.agent.event.BrowseEvent;
import org.snapscript.agent.event.ExecuteData;
import org.snapscript.agent.event.ExecuteEvent;
import org.snapscript.agent.event.PingEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.StepEvent;

public class ProcessEventReceiver extends ProcessEventAdapter {
   
   private final AtomicReference<ExecuteData> reference;
   private final ConnectionChecker checker;
   private final ResourceExecutor executor;
   private final ProcessContext context;
   
   public ProcessEventReceiver(ProcessContext context, ConnectionChecker checker) throws Exception {
      this.reference = new AtomicReference<ExecuteData>();
      this.executor = new ResourceExecutor(context);
      this.checker = checker;
      this.context = context;
   }

   @Override
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {
      ExecuteData data = event.getData();
      
      if(reference.compareAndSet(null, data)) { // execute only once
         Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
         BreakpointMatcher matcher = context.getMatcher();
         ProcessStore store = context.getStore();
         String actual = context.getProcess();
         String target = data.getProcess();
         String project = data.getProject();
         String resource = data.getResource();
         
         if(!target.equals(actual)) {
            throw new IllegalArgumentException("Process '" +actual+ "' received event for '"+target+"'");
         }
         matcher.update(breakpoints);
         store.update(project); // XXX rubbish
         executor.execute(channel, project, resource);
      }
   }
   
   @Override
   public void onBreakpoints(ProcessEventChannel channel, BreakpointsEvent event) throws Exception {
      Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
      BreakpointMatcher matcher = context.getMatcher();
      matcher.update(breakpoints);
   }
   
   @Override
   public void onStep(ProcessEventChannel channel, StepEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.getThread();
      int type = event.getType();
      
      if(type == StepEvent.RUN) {
         controller.resume(ResumeType.RUN, thread);
      } else if(type == StepEvent.STEP_IN) {
         controller.resume(ResumeType.STEP_IN, thread);
      } else if(type == StepEvent.STEP_OUT) {
         controller.resume(ResumeType.STEP_OUT, thread);
      } else if(type == StepEvent.STEP_OVER) {
         controller.resume(ResumeType.STEP_OVER, thread);
      }
   }
   
   @Override
   public void onBrowse(ProcessEventChannel channel, BrowseEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.getThread();
      Set<String> expand = event.getExpand();
      
      controller.browse(expand, thread);
   }

   @Override
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {
      ExecuteData data = reference.get();
      
      if(data != null) {
         String project = data.getProject();
         String resource = data.getResource();
         
         checker.update(channel, event, project, resource);
      } else {
         checker.update(channel, event, null, null); 
      }
   }

   @Override
   public void onClose(ProcessEventChannel channel) throws Exception {
      System.exit(0);
   }
}
