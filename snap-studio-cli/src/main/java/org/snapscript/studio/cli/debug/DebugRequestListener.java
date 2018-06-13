package org.snapscript.studio.cli.debug;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.core.module.Path;
import org.snapscript.studio.agent.DebugAgent;
import org.snapscript.studio.agent.DebugClient;
import org.snapscript.studio.agent.DebugContext;
import org.snapscript.studio.agent.log.LogLevel;

public class DebugRequestListener {

   private final AtomicReference<DebugClient> reference;
   private final ConnectLauncher launcher;
   private final DebugContext context;
   private final AtomicBoolean active;
   private final Path script;
   
   public DebugRequestListener(DebugContext context, Path script, int port) {
      this.launcher = new ConnectLauncher(context, this, port);
      this.reference = new AtomicReference<DebugClient>();
      this.active = new AtomicBoolean();
      this.context = context;
      this.script = script;
   }

   public String attachRequest(AttachRequest request) {
      String level = LogLevel.INFO.name();
      String project = request.getProject();
      URI root = request.getTarget();
      String path = script.getPath();
      
      if(!isAttached()) {
         try {
            DebugAgent agent = new DebugAgent(context, level);
            DebugClient client = agent.start(root, launcher);
            
            reference.set(client);
            client.attachProcess(project, path);
         }catch(Exception e){
            e.printStackTrace();
         }
      }
      return context.getProcess();
   }
   
   public String detachRequest(DetachRequest request) {
      DebugClient client = reference.getAndSet(null);
      
      try {
         if(client != null) {
            client.detachClient();
         }
      }catch(Exception e){
         e.printStackTrace();
      }
      return context.getProcess(); 
   }
   
   public void onReset() {
      DebugClient client = reference.getAndSet(null);
      
      try {
         if(client != null) {
            client.detachClient();
         }
      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
   public boolean isAttached(){
      return reference.get() != null;
   }
   
   public void start() {
      launcher.run();
   }
   
   private class ConnectLauncher implements Runnable {
      
      private final DebugRequestListener listener;
      private final ConnectAcceptor acceptor;
      private final ThreadFactory factory;
      
      public ConnectLauncher(DebugContext context, DebugRequestListener listener, int port) {
         this.acceptor = new ConnectAcceptor(listener, port);
         this.factory = new ThreadBuilder();
         this.listener = listener;
      }

      @Override
      public void run() {
         listener.onReset();
         
         if(active.compareAndSet(false, true)) {
            Thread thread = factory.newThread(acceptor);
            thread.start();
         }
      }      
   }
   
   private class ConnectAcceptor implements Runnable {
      
      private final DebugRequestConsumer consumer;
      private final int port;
      
      public ConnectAcceptor(DebugRequestListener listener, int port) {
         this.consumer = new DebugRequestConsumer(listener);
         this.port = port;
      }
      
      public void run() {
         try {
            ServerSocket listener = new ServerSocket(port);
            
            try {
               while(active.get()) {
                  Socket socket = listener.accept();
      
                  try {
                     consumer.consume(socket);
                  }catch(Exception e) {
                     e.printStackTrace();
                  }finally {
                     socket.close();
                  }
               }
            } finally {
               listener.close();
            }
         } catch(Exception e){
            e.printStackTrace();
         } finally {
            active.set(false);
         }
         
      }
   }
}
