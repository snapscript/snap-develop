package org.snapscript.studio.agent;

import java.net.URI;

import org.snapscript.core.scope.EmptyModel;
import org.snapscript.core.scope.Model;
import org.snapscript.core.trace.TraceInterceptor;
import org.snapscript.studio.agent.debug.BreakpointMatcher;
import org.snapscript.studio.agent.debug.FaultContextExtractor;
import org.snapscript.studio.agent.debug.ResumeType;
import org.snapscript.studio.agent.debug.SuspendController;
import org.snapscript.studio.agent.debug.SuspendInterceptor;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProcessEventTimer;
import org.snapscript.studio.agent.event.RegisterEvent;
import org.snapscript.studio.agent.event.client.ConnectTunnelClient;
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
   
   public DebugClient start(final URI root, final Runnable task) throws Exception {
      return start(root, task, model);
   }
   
   public DebugClient start(final URI root, final Runnable task, final Model model) throws Exception {
      return start(root, task, model, log);
   }
   
   public DebugClient start(final URI root, final Runnable task, final Model model, final Log log) throws Exception {
      final BreakpointMatcher matcher = context.getMatcher();
      final SuspendController controller = context.getController();
      final TraceInterceptor interceptor = context.getInterceptor();
      final TraceProfiler profiler = context.getProfiler();
      final String process = context.getProcess();
      final String system = context.getSystem();
      final RunMode mode = context.getMode();
      final String host = root.getHost();
      final int port = root.getPort();
      
      try {
         final AsyncLog adapter = new AsyncLog(log, level);
         final TraceLogger logger = new LogLogger(adapter, level);
         final CompileValidator validator = new CompileValidator(context);
         final ConnectionChecker checker = new ConnectionChecker(context, process, system);
         final ProcessExecutor executor = new ProcessExecutor(context, checker, logger, mode, model);
         final ProcessEventReceiver listener = new ProcessEventReceiver(context, checker, executor);
         final ProcessEventTimer timer = new ProcessEventTimer(listener, logger);
         final ConnectTunnelClient client = new ConnectTunnelClient(timer, checker, logger);
         final ProcessEventChannel channel = client.connect(process, host, port);
         final SuspendInterceptor suspender = new SuspendInterceptor(channel, matcher, controller, mode, process);
         final FaultContextExtractor extractor = new FaultContextExtractor(channel, logger, process);
         final RegisterEvent register = new RegisterEvent.Builder(process)
            .withSystem(system)
            .build();
         
         interceptor.register(profiler);
         interceptor.register(suspender);
         interceptor.register(extractor);
         channel.send(register); // send the initial register event
         validator.validate();
         checker.register(new ConnectionListener() {
           
            @Override
            public void onClose(){
               try {
                  adapter.stop();
                  checker.close();
                  controller.resume(ResumeType.RUN);
                  interceptor.remove(profiler);
                  interceptor.remove(suspender);
                  interceptor.remove(extractor);
                  controller.resume(ResumeType.RUN);
                  channel.close("Stop requested");
               }catch(Exception e) {
                  logger.info("Error stopping client", e);
               } finally {
                  try {
                     task.run();
                  }catch(Exception e){
                     logger.info("Error executing completion task", e);
                  }
               }
            }
         });
         checker.start();
         
         return new DebugClient(context, channel, executor, process);
      } catch (Exception e) {
         throw new IllegalStateException("Could not start process '" + process+ "'", e);
      }
   }
}