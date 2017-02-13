package org.snapscript.develop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.simpleframework.transport.Channel;
import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.FaultEvent;
import org.snapscript.agent.event.PongEvent;
import org.snapscript.agent.event.ProcessEventAdapter;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventListener;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.agent.event.SyntaxErrorEvent;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.event.WriteOutputEvent;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadBuilder;
import org.snapscript.develop.configuration.ProcessConfiguration;
import org.snapscript.develop.tunnel.ProcessEventService;

public class ProcessPool {
   
   private static final long DEFAULT_WAIT_TIME = 10000;
   private static final long DEFAULT_PING_FREQUENCY = 5000;
   
   private final BlockingQueue<ProcessConnection> running;
   private final Set<ProcessEventListener> listeners;
   private final ProcessConfiguration configuration;
   private final ProcessEventInterceptor interceptor;
   private final ProcessConnectionPool connections;
   private final ProcessAgentStarter starter;
   private final ProcessEventService router;
   private final ProcessLauncher launcher;
   private final ProcessAgentPinger pinger;
   private final ProcessNameFilter filter;
   private final ConsoleManager manager;
   private final ProcessListener listener;
   private final ProcessLogger logger;
   private final ThreadFactory factory;
   private final int capacity;
   
   public ProcessPool(ProcessConfiguration configuration, ProcessLogger logger, Workspace workspace, int capacity) throws IOException {
      this(configuration, logger, workspace, capacity, DEFAULT_PING_FREQUENCY);
   }
   
   public ProcessPool(ProcessConfiguration configuration, ProcessLogger logger, Workspace workspace, int capacity, long frequency) throws IOException {
      this.connections = new ProcessConnectionPool(logger);
      this.listeners = new CopyOnWriteArraySet<ProcessEventListener>();
      this.running = new LinkedBlockingQueue<ProcessConnection>();
      this.interceptor = new ProcessEventInterceptor(listeners);
      this.router = new ProcessEventService(interceptor, logger);
      this.launcher = new ProcessLauncher(router, logger, workspace);
      this.pinger = new ProcessAgentPinger(frequency);
      this.starter = new ProcessAgentStarter(pinger);
      this.filter = new ProcessNameGenerator();
      this.listener = new ProcessListener(logger);
      this.manager = new ConsoleManager(listener, frequency);
      this.factory = new ThreadBuilder();
      this.configuration = configuration;
      this.capacity = capacity;
      this.logger = logger;
   }
   
   public ProcessConnection acquire(String process) {
      try {
         ProcessConnection connection = connections.acquire(DEFAULT_WAIT_TIME, process); // take a process from the pool
         
         if(connection == null) {
            if(process == null) {
               throw new IllegalStateException("No agent found as pool is empty");
            }
            throw new IllegalStateException("No agent '" + process + "' as pool is empty");
         }
         running.offer(connection);
         launch(); // start a process straight away
         return connection;
      }catch(Exception e){
         logger.info("Could not acquire process", e);
      }
      return null;
   }
   
   public void register(ProcessEventListener listener) {
      try {
         listeners.add(listener);
      }catch(Exception e){
         logger.info("Could not register process listener", e);
      }
   }
   
   public void remove(ProcessEventListener listener) {
      try {
         listeners.remove(listener);
      }catch(Exception e){
         logger.info("Could not remove process listener", e);
      }
   }
   
   public boolean ping(String process, long time) {
      try {
         ProcessConnection connection = connections.acquire(0, process); // take a process from the pool
         
         if(connection != null) {
            if(connection.ping(time)) {
               connections.register(connection); // add back if ping succeeded
               return true;
            }
            connection.close();
         }
      }catch(Exception e) {
         logger.info("Could not ping '" + process + "'", e);
      }
      return false;
   }
   
   public void connect(Channel channel) {
      try {
         router.connect(channel);
      } catch(Exception e) {
         logger.info("Could not connect channel", e);
      }
   }
   
   public void start(String host, int port) { // http://host:port/project
      try {
         manager.start();
         pinger.start(host, port);
      } catch(Exception e) {
         logger.info("Could not start pool on port " + port, e);
      }
   }
   
   public void launch() { // launch a new process!!
      try {
         if(capacity > 0){
            Thread thread = factory.newThread(starter);
            thread.start();
         }
      } catch(Exception e) {
         logger.info("Could not launch process", e);
      }
   }
   
   private class ProcessEventInterceptor extends ProcessEventAdapter {
      
      private final Set<ProcessEventListener> listeners;
      
      public ProcessEventInterceptor(Set<ProcessEventListener> listeners) {
         this.listeners = listeners;
      }
      
      @Override
      public void onRegister(ProcessEventChannel channel, RegisterEvent event) throws Exception {
         String process = event.getProcess();
         ProcessConnection connection = new ProcessConnection(channel, logger, process);
         connections.register(connection);

         for(ProcessEventListener listener : listeners) {
            try {
               listener.onRegister(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing exit event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onExit(ProcessEventChannel channel, ExitEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onExit(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing exit event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onWriteError(ProcessEventChannel channel, WriteErrorEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onWriteError(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing write error event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onWriteOutput(ProcessEventChannel channel, WriteOutputEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onWriteOutput(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing write output event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onSyntaxError(ProcessEventChannel channel, SyntaxErrorEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onSyntaxError(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing syntax error event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onBegin(ProcessEventChannel channel, BeginEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onBegin(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing begin event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onProfile(ProcessEventChannel channel, ProfileEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onProfile(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing profile event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onPong(ProcessEventChannel channel, PongEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onPong(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing pong event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onScope(ProcessEventChannel channel, ScopeEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onScope(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing scope event", e);
               listeners.remove(listener);
            }
         }
      }
      
      @Override
      public void onFault(ProcessEventChannel channel, FaultEvent event) throws Exception {
         String process = event.getProcess();
         
         for(ProcessEventListener listener : listeners) {
            try {
               listener.onFault(channel, event);
            } catch(Exception e) {
               logger.info(process + ": Exception processing fault event", e);
               listeners.remove(listener);
            }
         }
      }
   }
   
   private class ProcessAgentStarter implements Runnable {
      
      private final ProcessAgentPinger pinger;
      
      public ProcessAgentStarter(ProcessAgentPinger pinger) {
         this.pinger = pinger;
      }
      
      @Override
      public void run() {
         try {
            pinger.launch();
         }catch(Exception e) {
            logger.info("Error starting agent", e);
         }
      }
   }
   
   private class ProcessAgentPinger implements Runnable {
   
      private final AtomicInteger listen;
      private final long frequency;
      
      public ProcessAgentPinger(long frequency) {
         this.listen = new AtomicInteger();
         this.frequency = frequency;
      }
      
      public void start(String host, int port) {
         if(listen.compareAndSet(0, port)) {
            Thread thread = factory.newThread(this);
            
            configuration.setHost(host);
            configuration.setPort(port);
            thread.start();
         }
      }
      
      @Override
      public void run() {
         while(true) {
            try {
               Thread.sleep(frequency);
               ping();
            }catch(Exception e) {
               logger.info("Error pinging agents", e);
            }
         }
      }
      
      public boolean launch() {
         try {
            int port = listen.get();

            if(port != 0) {
               ProcessDefinition definition = launcher.launch(configuration);
               Process process = definition.getProcess();
               String name = definition.getName();
               
               manager.tail(process, name);
               return true;
            }
         }catch(Exception e) {
            logger.info("Error launching agent", e);
         }
         return false;
      }
      
      public boolean kill() {
         try {
            int port = listen.get();

            if(port != 0) {
               ProcessConnection connection = connections.acquire(filter);

               if(connection != null) {
                  String name = connection.toString();
                  
                  logger.debug(name + ": Killing process");
                  connection.close();
               }
               return true;
            }
         }catch(Exception e) {
            logger.info("Error killing agent", e);
         }
         return false;
      }
      
      private void ping() {
         long time = System.currentTimeMillis();
         
         try {
            List<ProcessConnection> active = new ArrayList<ProcessConnection>();
            int require = capacity;

            while(!connections.isEmpty()) {
               ProcessConnection connection = connections.acquire(0);
               
               if(connection == null) {
                  break;
               }
               if(connection.ping(time)) {
                  active.add(connection);
               }
            }
            connections.register(active);

            int pool = connections.size();
            int remaining = require - pool; 
            
            if(remaining > 0) {
               launch(); // launch a new process at a time
            }
            if(remaining < 0 && require > 0) {
               kill(); // kill if pool grows too large
            }
            logger.debug("Ping has " + pool + " active from " + require);
            active.clear();
            
            while(!connections.isEmpty()) {
               ProcessConnection connection = running.poll();
               
               if(connection == null) {
                  break;
               }
               if(connection.ping(time)) {
                  active.add(connection);
               }
            }
            running.addAll(active);
         }catch(Exception e){
            logger.info("Error pinging agents", e);
         }
      }
   }
}
