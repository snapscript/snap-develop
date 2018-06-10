package org.snapscript.studio.agent.event.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.studio.agent.SingleThreadExecutor;
import org.snapscript.studio.agent.event.BeginEvent;
import org.snapscript.studio.agent.event.BreakpointsEvent;
import org.snapscript.studio.agent.event.BrowseEvent;
import org.snapscript.studio.agent.event.EvaluateEvent;
import org.snapscript.studio.agent.event.ExecuteEvent;
import org.snapscript.studio.agent.event.ExitEvent;
import org.snapscript.studio.agent.event.FaultEvent;
import org.snapscript.studio.agent.event.PingEvent;
import org.snapscript.studio.agent.event.PongEvent;
import org.snapscript.studio.agent.event.ProcessEvent;
import org.snapscript.studio.agent.event.ProcessEventChannel;
import org.snapscript.studio.agent.event.ProcessEventConnection;
import org.snapscript.studio.agent.event.ProcessEventConsumer;
import org.snapscript.studio.agent.event.ProcessEventListener;
import org.snapscript.studio.agent.event.ProcessEventProducer;
import org.snapscript.studio.agent.event.ProfileEvent;
import org.snapscript.studio.agent.event.RegisterEvent;
import org.snapscript.studio.agent.event.ScopeEvent;
import org.snapscript.studio.agent.event.ScriptErrorEvent;
import org.snapscript.studio.agent.event.StepEvent;
import org.snapscript.studio.agent.event.WriteErrorEvent;
import org.snapscript.studio.agent.event.WriteOutputEvent;
import org.snapscript.studio.agent.log.TraceLogger;

public class ProcessEventClient {
   
   private static final String THREAD_NAME = "%s: %s@%s:%s";
   
   private final ProcessEventListener listener;
   private final SingleThreadExecutor executor;
   private final TraceLogger logger;
   
   public ProcessEventClient(ProcessEventListener listener, TraceLogger logger) throws IOException {
      this.executor = new SingleThreadExecutor();
      this.listener = listener;
      this.logger = logger;
   }
   
   public ProcessEventChannel connect(String process, String host, int port) throws Exception {
      try {
         Socket socket = new Socket(host, port);
         ConnectTunnelHandler tunnel = new ConnectTunnelHandler(logger, process, port);
         InputStream input = socket.getInputStream();
         OutputStream output = socket.getOutputStream();
         String threadName = String.format(THREAD_NAME, SocketConnection.class.getSimpleName(), process, host, port);
         SocketConnection connection = new SocketConnection(socket, input, output, threadName);
      
         tunnel.tunnel(socket); // do the tunnel handshake
         socket.setSoTimeout(10000);
         connection.start();
         return connection;
      }catch(Exception e) {
         throw new IllegalStateException("Could not connect to " + host + ":" + port, e);
      }
   }

   private class SocketConnection extends Thread implements ProcessEventChannel {
      
      private final ProcessEventConnection connection;
      private final AtomicBoolean open;
      private final Set<Class> events;
      private final Socket socket;
      
      public SocketConnection(Socket socket, InputStream input, OutputStream output, String threadName) throws IOException {
         this.connection = new ProcessEventConnection(logger, executor, input, output, socket);
         this.events = new CopyOnWriteArraySet<Class>();
         this.open = new AtomicBoolean(true);
         this.setName(threadName);
         this.socket = socket;
      }
      
      @Override
      public boolean send(ProcessEvent event) throws Exception {
         ProcessEventProducer producer = connection.getProducer();
         String process = event.getProcess();
         
         try {
            producer.produce(event);
            return true;
         } catch(Exception e) {
            logger.info(process + ": Error sending event", e);
            close(process + ": Error sending event " +event + ": " + e);
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
            logger.info(process + ": Error sending async event", e);
            close(process + ": Error sending async event " +event + ": " + e);
         }
         return false;
      }
      
      @Override
      public void run() {
         try {
            ProcessEventConsumer consumer = connection.getConsumer();
            
            while(true) {
               ProcessEvent event = consumer.consume();
               Class type = event.getClass();
               
               events.add(type);
               
               if(event instanceof ExitEvent) {
                  listener.onExit(this, (ExitEvent)event);
               } else if(event instanceof ExecuteEvent) {
                  listener.onExecute(this, (ExecuteEvent)event);                  
               } else if(event instanceof RegisterEvent) {
                  listener.onRegister(this, (RegisterEvent)event);
               } else if(event instanceof ScriptErrorEvent) {
                  listener.onScriptError(this, (ScriptErrorEvent)event);
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
               } else if(event instanceof FaultEvent) {
                  listener.onFault(this, (FaultEvent)event);
               }
            }
         }catch(Exception e) {
            logger.info("Error processing events ["+ events + "]", e);
            close("Error in event loop: " + e);
         } finally {
            close("Event loop has finished");
         }
      }
      
      @Override
      public void close(String reason) {
         try {
            ProcessEventProducer producer = connection.getProducer();
            
            if(open.compareAndSet(true, false)) {
               listener.onClose(this);
               producer.close(reason);
            }
            socket.close();
         } catch(Exception e) {
            logger.info("Error closing client connection", e);
         }
      }
   }
}