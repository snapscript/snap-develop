package org.snapscript.agent.event.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.agent.event.*;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.common.ThreadBuilder;

public class SocketEventServer implements ProcessEventChannel {

   private final Map<String, ProcessEventChannel> receivers;
   private final ProcessEventListener listener;
   private final ProcessEventExecutor executor;
   private final SocketAcceptor acceptor;
   private final ProcessLogger logger;
   private final ThreadFactory factory;
   
   public SocketEventServer(ProcessEventListener listener, ProcessLogger logger, int port) throws IOException {
      this.receivers = new ConcurrentHashMap<String, ProcessEventChannel>();
      this.executor = new ProcessEventExecutor();
      this.acceptor = new SocketAcceptor(port);
      this.factory = new ThreadBuilder();
      this.listener = listener;
      this.logger = logger;
   }
   
   @Override
   public boolean send(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      ProcessEventChannel channel = receivers.get(process);
      
      if(channel == null) {
         throw new IllegalArgumentException("No channel for " + process);
      }
      return channel.send(event);
   }

   @Override
   public boolean sendAsync(ProcessEvent event) throws Exception {
      String process = event.getProcess();
      ProcessEventChannel channel = receivers.get(process);

      if(channel == null) {
         throw new IllegalArgumentException("No channel for " + process);
      }
      return channel.sendAsync(event);
   }

   @Override
   public int port() throws Exception {
      return acceptor.port();
   }
   
   public void start() throws Exception {
      acceptor.start();
   }
   
   @Override
   public void close() throws Exception {
      acceptor.close();
   }
   
   private class SocketAcceptor implements Runnable {
      
      private final ServerSocket server;
      
      public SocketAcceptor(int port) throws IOException {
         this.server = new ServerSocket(port);
      }
      
      @Override
      public void run() {
         try {
            int port = server.getLocalPort();
            
            logger.info("agent-port="+port);
            
            while(true) {
               Socket socket = server.accept();
               InputStream input = socket.getInputStream();
               OutputStream output = socket.getOutputStream();
               
               try {
                  SocketConnection connection = new SocketConnection(socket, input, output);
               
                  socket.setSoTimeout(10000);
                  connection.start();
               } catch(Exception e) {
                  socket.close();
               }
            }
         }catch(Exception e) {
            logger.info("Error listening for connections", e);
         }
      }
      
      public int port() {
         return server.getLocalPort();
      }
      
      public void start() {
         try {
            Thread thread = factory.newThread(this);
            thread.start();
         }catch(Exception e){
            logger.info("Error starting acceptor", e);
         }
      }
      
      public void close() {
         try {
            server.close();
         }catch(Exception e){
            logger.info("Error closing acceptor", e);
         }
      }
   }
   
   private class SocketConnection implements ProcessEventChannel, Runnable {
      
      private final ProcessEventConnection connection;
      private final AtomicBoolean active;
      private final AtomicBoolean open;
      private final Socket socket;
      
      public SocketConnection(Socket socket, InputStream input, OutputStream output) throws IOException {
         this.connection = new ProcessEventConnection(executor, input, output);
         this.open = new AtomicBoolean(true);
         this.active = new AtomicBoolean();
         this.socket = socket;
      }
      
      @Override
      public boolean send(ProcessEvent event) throws Exception {
         String process = event.getProcess();
         ProcessEventProducer producer = connection.getProducer();
         
         try {
            producer.produce(event);
            return true;
         } catch(Exception e) {
            logger.info(process + ": Error sending event", e);
            receivers.remove(process);
            close();
         }
         return false;
      }

      @Override
      public boolean sendAsync(ProcessEvent event) throws Exception {
         ProcessEventProducer producer = connection.getProducer();
         String process = event.getProcess();

         try {
            Future<Boolean> future = producer.produceAsync(event);
            return future.get();
         } catch(Exception e) {
            logger.info(process + ": Error sending event", e);
            close();
         }
         return false;
      }
      
      @Override
      public void run() {
         try {
            ProcessEventConsumer consumer = connection.getConsumer();
            
            while(true) {
               ProcessEvent event = consumer.consume();
               String process = event.getProcess();
               
               receivers.put(process, this);
               
               if(event instanceof ExitEvent) {
                  listener.onExit(this, (ExitEvent)event);
               } else if(event instanceof ExecuteEvent) {
                  listener.onExecute(this, (ExecuteEvent)event);                  
               } else if(event instanceof RegisterEvent) {
                  listener.onRegister(this, (RegisterEvent)event);
               } else if(event instanceof SyntaxErrorEvent) {
                  listener.onSyntaxError(this, (SyntaxErrorEvent)event);
               } else if(event instanceof WriteErrorEvent) {
                  listener.onWriteError(this, (WriteErrorEvent)event);
               } else if(event instanceof WriteOutputEvent) {
                  listener.onWriteOutput(this, (WriteOutputEvent)event);
               } else if(event instanceof PingEvent) {
                  listener.onPing(this, (PingEvent)event);
               } else if(event instanceof PongEvent) {
                  listener.onPong(this, (PongEvent)event);
               } else if(event instanceof ScopeEvent) {
                  listener.onScope(this, (ScopeEvent)event);
               } else if(event instanceof BreakpointsEvent) {
                  listener.onBreakpoints(this, (BreakpointsEvent)event);
               } else if(event instanceof BeginEvent) {
                  listener.onBegin(this, (BeginEvent)event);
               } else if(event instanceof StepEvent) {
                  listener.onStep(this, (StepEvent)event);
               } else if(event instanceof BrowseEvent) {
                  listener.onBrowse(this, (BrowseEvent)event);
               } else if(event instanceof EvaluateEvent) {
                  listener.onEvaluate(this, (EvaluateEvent)event);                  
               } else if(event instanceof ProfileEvent) {
                  listener.onProfile(this, (ProfileEvent)event);
               } else if(event instanceof EvaluateEvent) {
                  listener.onEvaluate(this, (EvaluateEvent)event);
               } else if(event instanceof FaultEvent) {
                  listener.onFault(this, (FaultEvent)event);
               }
            }
         }catch(Exception e) {
            logger.info("Error listening for events", e);
         }finally {
            close();
         }
      }
      
      public void start() throws Exception {
         try {
            if(active.compareAndSet(false, true)) {
               Thread thread = factory.newThread(this);
               thread.start();
            }
         }catch(Exception e) {
            logger.info("Could not start server", e);
         }
      }
      
      @Override
      public int port() throws Exception {
         try {
            return socket.getLocalPort();
         } catch(Exception e) {
            logger.info("Error getting local port", e);
         }
         return -1;
      }
      
      @Override
      public void close() {
         try {
            ProcessEventProducer producer = connection.getProducer();
            
            if(open.compareAndSet(true, false)) {
               listener.onClose(this);
               producer.close();
            }
            socket.close();
         } catch(Exception e) {
            logger.info("Error closing socket", e);
         }
      }
   }
}
