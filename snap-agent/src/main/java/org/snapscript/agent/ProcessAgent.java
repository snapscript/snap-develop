package org.snapscript.agent;

import java.net.URI;

import org.snapscript.agent.debug.BreakpointMatcher;
import org.snapscript.agent.debug.ErrorStateExtractor;
import org.snapscript.agent.debug.SuspendController;
import org.snapscript.agent.debug.SuspendInterceptor;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventTimer;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.socket.SocketEventClient;
import org.snapscript.agent.profiler.ProcessProfiler;
import org.snapscript.core.trace.TraceInterceptor;

public class ProcessAgent {

   private final ProcessContext context;
   private final String process;
   private final String level;
   private final URI root;
   private final int port;

   public ProcessAgent(URI root, String process, String level, int port) {
      this(root, process, level, port, 0);
   }
   
   public ProcessAgent(URI root, String process, String level, int port, int threads) {
      this(root, process, level, port, threads, 0);
   }
   
   public ProcessAgent(URI root, String process, String level, int port, int threads, int stack) {
      this.context = new ProcessContext(root, process, port, threads, stack);
      this.process = process;
      this.level = level;
      this.root = root;
      this.port = port;
   }
   
   public void start() throws Exception {
      BreakpointMatcher matcher = context.getMatcher();
      SuspendController controller = context.getController();
      TraceInterceptor interceptor = context.getInterceptor();
      ProcessProfiler profiler = context.getProfiler();
      String system = System.getProperty("os.name");
      String host = root.getHost();
      
      try {
         ConsoleLogger logger = new ConsoleLogger(level);
         ErrorStateExtractor extractor = new ErrorStateExtractor(logger);
         SystemValidator validator = new SystemValidator(context);
         ConnectionChecker checker = new ConnectionChecker(process, system);
         ProcessEventReceiver listener = new ProcessEventReceiver(context, checker);
         ProcessEventTimer timer = new ProcessEventTimer(listener, logger);
         SocketEventClient client = new SocketEventClient(timer, logger);
         ProcessEventChannel channel = client.connect(host, port);
         SuspendInterceptor suspender = new SuspendInterceptor(channel, matcher, controller, process);
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
