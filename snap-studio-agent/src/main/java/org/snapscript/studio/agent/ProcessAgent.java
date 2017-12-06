package org.snapscript.studio.agent;

import java.net.URI;

import org.snapscript.core.EmptyModel;
import org.snapscript.core.Model;
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
import org.snapscript.studio.agent.log.ProcessLogger;
import org.snapscript.studio.agent.profiler.ProcessProfiler;

public class ProcessAgent {

   private final ProcessContext context;
   private final ProcessMode mode;
   private final String system;
   private final String process;
   private final String level;
   private final Model model;
   private final URI root;

   public ProcessAgent(ProcessMode mode, URI root, String system, String process, String level) {
      this(mode, root, system, process, level, 10);
   }
   
   public ProcessAgent(ProcessMode mode, URI root,String system,  String process, String level, int threads) {
      this(mode, root, system, process, level, threads, 0);
   }
   
   public ProcessAgent(ProcessMode mode, URI root, String system, String process, String level, int threads, int stack) {
      this.context = new ProcessContext(mode, root, process, threads, stack);
      this.model = new EmptyModel();
      this.process = process;
      this.system = system;
      this.level = level;
      this.root = root;
      this.mode = mode;
   }
   
   public ProcessAgentService start() throws Exception {
      return start(model);
   }
   
   public ProcessAgentService start(Model model) throws Exception {
      BreakpointMatcher matcher = context.getMatcher();
      SuspendController controller = context.getController();
      TraceInterceptor interceptor = context.getInterceptor();
      ProcessProfiler profiler = context.getProfiler();
      String host = root.getHost();
      int port = root.getPort();
      
      try {
         Log log = new ConsoleLog();
         Log adapter = new AsyncLog(log);
         ProcessLogger logger = new LogLogger(adapter, level);
         SystemValidator validator = new SystemValidator(context);
         ConnectionChecker checker = new ConnectionChecker(context, process, system);
         ProcessResourceExecutor executor = new ProcessResourceExecutor(context, mode, model);
         ProcessEventReceiver listener = new ProcessEventReceiver(context, mode, checker, executor, model);
         ProcessEventTimer timer = new ProcessEventTimer(listener, logger);
         ProcessEventClient client = new ProcessEventClient(timer, logger);
         ProcessEventChannel channel = client.connect(host, port);
         SuspendInterceptor suspender = new SuspendInterceptor(channel, matcher, controller, process);
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
         
         return new ProcessAgentService(context, channel, executor, mode, model);
      } catch (Exception e) {
         throw new IllegalStateException("Could not start process '" + process+ "'", e);
      }
   }
}