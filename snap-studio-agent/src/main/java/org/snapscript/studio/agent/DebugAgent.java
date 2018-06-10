package org.snapscript.studio.agent;

import java.net.URI;

import org.snapscript.core.scope.EmptyModel;
import org.snapscript.core.scope.Model;
import org.snapscript.core.trace.TraceInterceptor;
import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.debug.FaultContextExtractor;
import org.snapscript.studio.agent.debug.SuspendController;
import org.snapscript.studio.agent.debug.SuspendInterceptor;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProcessEventTimer;
import org.snapscript.studio.agent.event.RegisterEvent;
import org.snapscript.studio.agent.event.client.ProcessEventClient;
import org.snapscript.studio.agent.log.AsyncLog;
import org.snapscript.studio.agent.log.ConsoleLog;
import org.snapscript.studio.agent.log.Log;
import org.snapscript.studio.agent.log.LogLogger;
import org.snapscript.studio.agent.log.TraceLogger;
import org.snapscript.studio.agent.profiler.TraceProfiler;
import org.snapscript.studio.agent.task.ProcessExecutor;

public class DebugAgent {

   private final DebugContext context;
   private final String level;
   private final Model model;
   private final Log log;

   public DebugAgent(DebugContext context, String level) {
      this.model = new EmptyModel();
      this.log = new ConsoleLog();
      this.context = context;
      this.level = level;
   }
   
   public DebugClient start(URI root, Runnable task) throws Exception {
      return start(root, task, model);
   }
   
   public DebugClient start(URI root, Runnable task, Model model) throws Exception {
      return start(root, task, model, log);
   }
   
   public DebugClient start(URI root, Runnable task, Model model, Log log) throws Exception {
      BreakpointMatcher matcher = context.getMatcher();
      SuspendController controller = context.getController();
      TraceInterceptor interceptor = context.getInterceptor();
      TraceProfiler profiler = context.getProfiler();
      String process = context.getProcess();
      String system = context.getSystem();
      RunMode mode = context.getMode();
      String host = root.getHost();
      int port = root.getPort();
      
      try {
         Log adapter = new AsyncLog(log, level);
         TraceLogger logger = new LogLogger(adapter, level);
         CompileValidator validator = new CompileValidator(context);
         ConnectionChecker checker = new ConnectionChecker(context, task, process, system);
         ProcessExecutor executor = new ProcessExecutor(context, logger, mode, model);
         ProcessEventReceiver listener = new ProcessEventReceiver(context, mode, checker, executor, model);
         ProcessEventTimer timer = new ProcessEventTimer(listener, logger);
         ProcessEventClient client = new ProcessEventClient(timer, logger);
         ProcessEventChannel channel = client.connect(process, host, port);
         SuspendInterceptor suspender = new SuspendInterceptor(channel, matcher, controller, mode, process);
         FaultContextExtractor extractor = new FaultContextExtractor(channel, logger, process);
         RegisterEvent register = new RegisterEvent.Builder(process)
            .withSystem(system)
            .build();
         
         interceptor.register(profiler);
         interceptor.register(suspender);
         interceptor.register(extractor);
         channel.send(register); // send the initial register event
         validator.validate();
         checker.start();
         
         return new DebugClient(context, channel, executor, mode, model);
      } catch (Exception e) {
         throw new IllegalStateException("Could not start process '" + process+ "'", e);
      }
   }
}