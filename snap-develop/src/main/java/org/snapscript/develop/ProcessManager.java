package org.snapscript.develop;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.transport.Channel;
import org.snapscript.agent.event.ProcessEventFilter;
import org.snapscript.agent.event.ProcessEventListener;
import org.snapscript.agent.event.StepEvent;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.command.BreakpointsCommand;
import org.snapscript.develop.command.BrowseCommand;
import org.snapscript.develop.command.EvaluateCommand;
import org.snapscript.develop.command.ExecuteCommand;
import org.snapscript.develop.command.StepCommand;
import org.snapscript.develop.configuration.ProcessConfiguration;
import org.snapscript.develop.configuration.ProcessConfigurationLoader;

public class ProcessManager {
   
   private final Map<String, ProcessConnection> connections; // active processes
   private final ProcessConfiguration configuration;
   private final ProcessConfigurationLoader loader;
   private final ProcessPool pool;

   public ProcessManager(ProcessConfigurationLoader loader, ProcessLogger logger, Workspace workspace, int capacity) throws Exception {
      this.connections = new ConcurrentHashMap<String, ProcessConnection>();
      this.configuration = new ProcessConfiguration();
      this.pool = new ProcessPool(configuration, logger, workspace, capacity);
      this.loader = loader;
   }
   
   public void connect(Channel channel) {
      pool.connect(channel);
   }
   
   public void register(ProcessEventListener listener) {
      pool.register(listener);
   }
   
   public void remove(ProcessEventListener listener) {
      pool.remove(listener);
   }
   
   public boolean execute(ExecuteCommand command) {
      return execute(command, null);
   }
   
   public boolean execute(ExecuteCommand command, ProcessEventFilter filter) { 
      String focus = filter.getFocus();
      ProcessConnection connection = pool.acquire(focus);
      
      if(connection != null) {
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         String project = command.getProject();
         String resource = command.getResource();
         String process = connection.toString();
         
         if(filter != null) {
            filter.setFocus(process);
         }
         connections.put(process, connection);
         
         return connection.execute(project, resource, breakpoints);
      }
      return true;
   }
   
   public boolean breakpoints(BreakpointsCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         return connection.suspend(breakpoints);
      }
      return true;
   }
   
   public boolean browse(BrowseCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         Set<String> expand = command.getExpand();
         String thread = command.getThread();
         return connection.browse(thread, expand);
      }
      return true;
   }
   
   public boolean evaluate(EvaluateCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         Set<String> expand = command.getExpand();
         String expression = command.getExpression();
         String thread = command.getThread();
         boolean refresh = command.isRefresh();
         return connection.evaluate(thread, expression, refresh, expand);
      }
      return true;
   }
   
   public boolean step(StepCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         String thread = command.getThread();
         
         if(command.isRun()) {
            return connection.step(thread, StepEvent.RUN);
         } else if(command.isStepIn()) {
            return connection.step(thread, StepEvent.STEP_IN);
         } else if(command.isStepOut()) {
            return connection.step(thread, StepEvent.STEP_OUT);
         } else if(command.isStepOver()) {
            return connection.step(thread, StepEvent.STEP_OVER);
         }
      }
      return true;
   }
   
   public boolean stop(String process) {
      ProcessConnection connection = connections.remove(process);
      
      if(connection != null) {
         connection.close();
      }
      return true;
   }
   
   public boolean ping(String process, long time) {
      ProcessConnection connection = connections.get(process);

      if(connection != null) {
         return connection.ping(time);
      } 
      return pool.ping(process, time); // the process might not be active
   }
   
   public void start(String host, int port) {
      loader.load(configuration);
      configuration.setHost(host);
      configuration.setPort(port);
      pool.start(host, port);
   }
   
   public void launch() {
      pool.launch();
   }

}
