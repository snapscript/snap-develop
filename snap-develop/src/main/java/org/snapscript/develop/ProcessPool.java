package org.snapscript.develop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.snapscript.agent.ConsoleLogger;
import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.ExitEvent;
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
import org.snapscript.agent.event.socket.SocketEventServer;
import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.common.ThreadBuilder;
import org.snapscript.develop.configuration.ProcessConfiguration;

public class ProcessPool {

   private final Cache<String, BlockingQueue<ProcessConnection>> connections;
   private final BlockingQueue<ProcessConnection> running;
   private final Set<ProcessEventListener> listeners;
   private final ProcessConfiguration configuration;
   private final ProcessEventInterceptor interceptor;
   private final ProcessAgentStarter starter;
   private final ProcessLauncher launcher;
   private final ProcessAgentPinger pinger;
   private final SocketEventServer server;
   private final ConsoleManager manager;
   private final ProcessListener listener;
   private final ConsoleLogger logger;
   private final ThreadFactory factory;
   private final int capacity;
   
   public ProcessPool(ProcessConfiguration configuration, ConsoleLogger logger, Workspace workspace, int port, int capacity) throws IOException {
      this(configuration, logger, workspace, port, capacity, 5000);
   }
   
   public ProcessPool(ProcessConfiguration configuration, ConsoleLogger logger, Workspace workspace, int port, int capacity, long frequency) throws IOException {
      this.connections = new LeastRecentlyUsedCache<String, BlockingQueue<ProcessConnection>>();
      this.listeners = new CopyOnWriteArraySet<ProcessEventListener>();
      this.running = new LinkedBlockingQueue<ProcessConnection>();
      this.interceptor = new ProcessEventInterceptor(listeners);
      this.server = new SocketEventServer(interceptor, logger, port);
      this.launcher = new ProcessLauncher(server, logger, workspace);
      this.pinger = new ProcessAgentPinger(frequency);
      this.starter = new ProcessAgentStarter(pinger);
      this.listener = new ProcessListener(logger);
      this.manager = new ConsoleManager(listener, frequency);
      this.factory = new ThreadBuilder();
      this.configuration = configuration;
      this.capacity = capacity;
      this.logger = logger;
   }
   
   public ProcessConnection acquire(String system) {
      try {
         BlockingQueue<ProcessConnection> pool = connections.fetch(system);
         
         if(pool == null) {
            throw new IllegalArgumentException("No pool of type '" + system + "'");
         }
         ProcessConnection connection = pool.poll(10, TimeUnit.SECONDS); // take a process from the pool
         
         if(connection == null) {
            throw new IllegalStateException("No agent of type " + system + " as pool is empty");
         }
         running.offer(connection);
         launch(); // start a process straight away
         return connection;
      }catch(Exception e){
         logger.info("Could not acquire process for '" +system+ "'", e);
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
   
   public void start(int port) { // http://host:port/project
      try {
         manager.start();
         server.start();
         pinger.start(port);
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
         String system = event.getSystem();
         ProcessConnection connection = new ProcessConnection(channel, logger, process);
         BlockingQueue<ProcessConnection> pool = connections.fetch(system);
         
         if(pool == null) {
            pool = new LinkedBlockingQueue<ProcessConnection>();
            connections.cache(system, pool);
         }
         pool.offer(connection);
         
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
      
      public void start(int port) {
         if(listen.compareAndSet(0, port)) {
            Thread thread = factory.newThread(this);
            
            configuration.setPort(port);
            thread.start();
         }
      }
      
      @Override
      public void run() {
         while(true) {
            try {
               String host = System.getProperty("os.name");
               BlockingQueue<ProcessConnection> pool = connections.fetch(host);
               
               if(pool == null) {
                  pool = new LinkedBlockingQueue<ProcessConnection>();
                  connections.cache(host, pool);
               }
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
            String system = System.getProperty("os.name"); // kill a host agent
            int port = listen.get();

            if(port != 0) {
               BlockingQueue<ProcessConnection> pool = connections.fetch(system);
               ProcessConnection connection = pool.poll();

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
         String host = System.getProperty("os.name");
         Set<String> systems = connections.keySet();
         long time = System.currentTimeMillis();
         
         try {
            List<ProcessConnection> active = new ArrayList<ProcessConnection>();
            int require = capacity;
            
            for(String system : systems) {
               BlockingQueue<ProcessConnection> available = connections.fetch(system);
               
               while(!connections.isEmpty()) {
                  ProcessConnection connection = available.poll();
                  
                  if(connection == null) {
                     break;
                  }
                  if(connection.ping(time)) {
                     active.add(connection);
                  }
               }
               available.addAll(active);
            }
            BlockingQueue<ProcessConnection> available = connections.fetch(host);
            int pool = available.size();
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
