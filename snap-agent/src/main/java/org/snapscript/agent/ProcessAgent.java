package org.snapscript.agent;

import java.net.URI;

import org.snapscript.agent.debug.BreakpointMatcher;
import org.snapscript.agent.debug.FaultContextExtractor;
import org.snapscript.agent.debug.SuspendController;
import org.snapscript.agent.debug.SuspendInterceptor;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventTimer;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.socket.SocketEventClient;
import org.snapscript.agent.log.ConsoleLog;
import org.snapscript.agent.log.ProcessLog;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.agent.profiler.ProcessProfiler;
import org.snapscript.core.EmptyModel;
import org.snapscript.core.Model;
import org.snapscript.core.trace.TraceInterceptor;

public class ProcessAgent {

   private final ProcessContext context;
   private final String system;
   private final String process;
   private final String level;
   private final Model model;
   private final URI root;
   private final int eventPort;

   public ProcessAgent(URI root, String system, String process, String level, int eventPort) {
      this(root, system, process, level, eventPort, 0);
   }
   
   public ProcessAgent(URI root,String system,  String process, String level, int eventPort, int threads) {
      this(root, system, process, level, eventPort, threads, 0);
   }
   
   public ProcessAgent(URI root, String system, String process, String level, int eventPort, int threads, int stack) {
      this.context = new ProcessContext(root, process, eventPort, threads, stack);
      this.model = new EmptyModel();
      this.eventPort = eventPort;
      this.process = process;
      this.system = system;
      this.level = level;
      this.root = root;
   }
   
   public void start(ProcessMode mode) throws Exception {
      start(mode, model);
   }
   
   public void start(ProcessMode mode, Model model) throws Exception {
      BreakpointMatcher matcher = context.getMatcher();
      SuspendController controller = context.getController();
      TraceInterceptor interceptor = context.getInterceptor();
      ProcessProfiler profiler = context.getProfiler();
      String host = root.getHost();
      int httpPort = root.getPort();
      
      try {
         ProcessLog log = new ConsoleLog();
         ProcessLogger logger = new ProcessLogger(log, level);
         SystemValidator validator = new SystemValidator(context);
         ConnectionChecker checker = new ConnectionChecker(process, system);
         ProcessEventReceiver listener = new ProcessEventReceiver(context, mode, checker, model);
         ProcessEventTimer timer = new ProcessEventTimer(listener, logger);
         SocketEventClient client = new SocketEventClient(timer, logger);
         ProcessEventChannel channel = client.connect(host, eventPort, httpPort);
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
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
