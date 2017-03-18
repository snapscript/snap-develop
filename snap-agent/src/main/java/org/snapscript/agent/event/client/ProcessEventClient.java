
package org.snapscript.agent.event.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.agent.event.BeginEvent;
import org.snapscript.agent.event.BreakpointsEvent;
import org.snapscript.agent.event.BrowseEvent;
import org.snapscript.agent.event.EvaluateEvent;
import org.snapscript.agent.event.ExecuteEvent;
import org.snapscript.agent.event.ExitEvent;
import org.snapscript.agent.event.FaultEvent;
import org.snapscript.agent.event.PingEvent;
import org.snapscript.agent.event.PongEvent;
import org.snapscript.agent.event.ProcessEvent;
import org.snapscript.agent.event.ProcessEventChannel;
import org.snapscript.agent.event.ProcessEventConnection;
import org.snapscript.agent.event.ProcessEventConsumer;
import org.snapscript.agent.event.ProcessEventExecutor;
import org.snapscript.agent.event.ProcessEventListener;
import org.snapscript.agent.event.ProcessEventProducer;
import org.snapscript.agent.event.ProfileEvent;
import org.snapscript.agent.event.RegisterEvent;
import org.snapscript.agent.event.ScopeEvent;
import org.snapscript.agent.event.StepEvent;
import org.snapscript.agent.event.SyntaxErrorEvent;
import org.snapscript.agent.event.WriteErrorEvent;
import org.snapscript.agent.event.WriteOutputEvent;
import org.snapscript.agent.log.ProcessLogger;

public class ProcessEventClient {
   
   private final ProcessEventListener listener;
   private final ProcessEventExecutor executor;
   private final ProcessLogger logger;
   
   public ProcessEventClient(ProcessEventListener listener, ProcessLogger logger) throws IOException {
      this.executor = new ProcessEventExecutor();
      this.listener = listener;
      this.logger = logger;
   }
   
   public ProcessEventChannel connect(String host, int port) throws Exception {
      try {
         Socket socket = new Socket(host, port);
         ProcessEventTunnel tunnel = new ProcessEventTunnel(logger, port);
         InputStream input = socket.getInputStream();
         OutputStream output = socket.getOutputStream();
         SocketConnection connection = new SocketConnection(socket, input, output);
      
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
      private final Socket socket;
      
      public SocketConnection(Socket socket, InputStream input, OutputStream output) throws IOException {
         this.connection = new ProcessEventConnection(logger, executor, input, output, socket);
         this.open = new AtomicBoolean(true);
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
               } else if(event instanceof FaultEvent) {
                  listener.onFault(this, (FaultEvent)event);
               }
            }
         }catch(Exception e) {
            logger.info("Error processing events", e);
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
