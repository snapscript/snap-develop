package org.snapscript.studio.agent;

import java.util.Map;
import java.util.Set;

import org.snapscript.core.Model;
import org.snapscript.core.trace.TraceInterceptor;
import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.debug.ResumeType;
import org.snapscript.studio.agent.debug.SuspendController;
import org.snapscript.studio.agent.event.BreakpointsEvent;
import org.snapscript.studio.agent.event.BrowseEvent;
import org.snapscript.studio.agent.event.EvaluateEvent;
import org.snapscript.studio.agent.event.ExecuteData;
import org.snapscript.studio.agent.event.ExecuteEvent;
import org.snapscript.studio.agent.event.PingEvent;
import org.snapscript.studio.agent.event.ProcessEventAdapter;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.StepEvent;

public class ProcessEventReceiver extends ProcessEventAdapter {
   
   private final ConnectionChecker checker;
   private final ProcessResourceExecutor executor;
   private final ProcessContext context;
   
   public ProcessEventReceiver(ProcessContext context, ProcessMode mode, ConnectionChecker checker, ProcessResourceExecutor executor, Model model) throws Exception {
      this.executor = executor;
      this.checker = checker;
      this.context = context;
   }

   @Override
   public void onExecute(ProcessEventChannel channel, ExecuteEvent event) throws Exception {
      ExecuteData data = event.getData();
      Map<String, Map<Integer, Boolean>> breakpoints = event.getBreakpoints();
      BreakpointMatcher matcher = context.getMatcher();
      TraceInterceptor interceptor = context.getInterceptor();
      ProcessStore store = context.getStore();
      String actual = context.getProcess();
      String dependencies = data.getDependencies();
      String target = data.getProcess();
      String project = data.getProject();
      String resource = data.getResource();
      boolean debug = data.isDebug();
      
      if(!target.equals(actual)) {
         throw new IllegalArgumentException("Process '" +actual+ "' received event for '"+target+"'");
      }
      if(!data.isDebug()) {
         interceptor.clear(); // disable interceptors
      }
      matcher.update(breakpoints);
      store.update(project); 
      executor.execute(channel, actual, project, resource, dependencies, debug);
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
   public void onEvaluate(ProcessEventChannel channel, EvaluateEvent event) throws Exception {
      SuspendController controller = context.getController();
      String thread = event.getThread();
      String expression = event.getExpression();
      Set<String> expand = event.getExpand();
      boolean refresh = event.isRefresh();
      
      controller.evaluate(expand, thread, expression, refresh);
   }

   @Override
   public void onPing(ProcessEventChannel channel, PingEvent event) throws Exception {
      ExecuteData data = executor.get();
      
      if(data != null) {
         String project = data.getProject();
         String resource = data.getResource();
         boolean debug = data.isDebug();
         
         checker.update(channel, event, project, resource, debug);
      } else {
         checker.update(channel, event, null, null, false); 
      }
   }

   @Override
   public void onClose(ProcessEventChannel channel) throws Exception {
      ProcessTerminator.terminate("Close event received");
   }
}