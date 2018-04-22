package org.snapscript.studio.service.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import org.simpleframework.transport.Channel;
import org.snapscript.studio.agent.event.ProcessEventFilter;
import org.snapscript.studio.agent.event.ProcessEventListener;
import org.snapscript.studio.agent.event.StepEvent;
import org.snapscript.studio.project.ClassPathFile;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.Workspace;
import org.snapscript.studio.project.config.ProcessConfiguration;
import org.snapscript.studio.project.config.ProcessConfigurationLoader;
import org.snapscript.studio.service.command.AttachCommand;
import org.snapscript.studio.service.command.BreakpointsCommand;
import org.snapscript.studio.service.command.BrowseCommand;
import org.snapscript.studio.service.command.EvaluateCommand;
import org.snapscript.studio.service.command.ExecuteCommand;
import org.snapscript.studio.service.command.StepCommand;
import org.snapscript.studio.service.command.StepCommand.StepType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessManager implements ProcessAgentController {
   
   private final Map<String, ProcessConnection> connections; // active processes
   private final ProcessConfiguration configuration;
   private final ProcessConfigurationLoader loader;
   private final ProcessPool pool;
   private final Workspace workspace;

   public ProcessManager(ProcessConfigurationLoader loader, Workspace workspace, @Value("${agent-pool}") int capacity) throws Exception {
      this.connections = new ConcurrentHashMap<String, ProcessConnection>();
      this.configuration = new ProcessConfiguration();
      this.pool = new ProcessPool(configuration, workspace, capacity);
      this.workspace = workspace;
      this.loader = loader;
   }
   
   public void connect(ProcessEventListener listener, Channel channel) {
      pool.register(listener);
      pool.connect(channel);
   }
   
   public void register(ProcessEventListener listener) {
      pool.register(listener);
   }
   
   public void remove(ProcessEventListener listener) {
      pool.remove(listener);
   }
   
   public boolean execute(ExecuteCommand command, ProcessEventFilter filter) { 
      String focus = filter.getFocus();
      ProcessConnection connection = pool.acquire(focus);
      
      if(connection != null) {
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         String projectName = command.getProject();
         Project project = workspace.getProject(projectName);
         ClassPathFile classPath = project.getClassPath();
         String dependencies = classPath.getPath();
         String resource = command.getResource();
         String process = connection.toString();
         boolean debug = command.isDebug();
         
         if(filter != null) {
            filter.setFocus(process);
         }
         connections.put(process, connection);
         
         return connection.execute(projectName, resource, dependencies, breakpoints, debug);
      }
      return true;
   }
   
   public boolean breakpoints(BreakpointsCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         String project = command.getProject();
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         return connection.suspend(project, breakpoints);
      }
      return true;
   }
   
   public boolean attach(AttachCommand command, String process) {
      ProcessConnection connection = connections.get(process);
      
      if(connection != null) {
         String project = command.getProject();
         Map<String, Map<Integer, Boolean>> breakpoints = command.getBreakpoints();
         return connection.suspend(project, breakpoints);
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
         StepType type = command.getType();
         
         if(type == StepType.RUN) {
            return connection.step(thread, StepEvent.RUN);
         } else if(type == StepType.STEP_IN) {
            return connection.step(thread, StepEvent.STEP_IN);
         } else if(type == StepType.STEP_OUT) {
            return connection.step(thread, StepEvent.STEP_OUT);
         } else if(type == StepType.STEP_OVER) {
            return connection.step(thread, StepEvent.STEP_OVER);
         }
      }
      return true;
   }
   
   @Override
   public boolean start(String process) { // move from waiting to running, used by agent
      if(!connections.containsKey(process)) {
         ProcessConnection connection = pool.acquire(process);
         
         if(connection != null) {
            connections.put(process, connection);
            return true;
         }
         return false;
      }
      return true; // already started
   }
   
   @Override
   public boolean stop(String process) {
      ProcessConnection connection = connections.remove(process);
      
      if(connection != null) {
         connection.close(process + ": Explicit stop requested");
      }
      return true;
   }

   @Override
   public boolean detach(String process) {
      ProcessConnection connection = connections.remove(process);

      if(connection != null) {
         log.debug(process + ": Detach requested");
      }
      return true;
   }
   
   @Override
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