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
   
   public void onAttachRequest(AttachRequest request) {
      DebugAgent agent = new DebugAgent(context, LogLevel.INFO.name());
      
      try {
         String project = request.getProject();
         URI root = request.getTarget();
         String path = script.getPath();
         DebugClient client = agent.start(root, launcher);
         
         client.attachProcess(project, path);
         reference.set(client);
      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
   public void onDetachRequest(DetachRequest request) {
      DebugClient client = reference.get();
      
      try {
         if(client != null) {
            client.detachClient();
         }
      }catch(Exception e){
         e.printStackTrace();
      }
   }
   
   public void start() {
      launcher.run();
   }
   
   private class ConnectLauncher implements Runnable {
      
      private final ConnectListener listener;
      private final ThreadFactory factory;
      
      public ConnectLauncher(DebugContext context, DebugRequestListener connector, int port) {
         this.listener = new ConnectListener(connector, port);
         this.factory = new ThreadBuilder();
      }

      @Override
      public void run() {
         if(active.compareAndSet(false, true)) {
            Thread thread = factory.newThread(listener);
            thread.start();
         }
      }      
   }
   
   private class ConnectListener implements Runnable {
      
      private final DebugRequestConsumer consumer;
      private final int port;
      
      public ConnectListener(DebugRequestListener listener, int port) {
         this.consumer = new DebugRequestConsumer(listener);
         this.port = port;
      }
      
      public void run() {
         try {
            ServerSocket listener = new ServerSocket(port);
            
            try {
               while(active.get()) {
                  Socket socket = listener.accept();
      
                  consumer.read(socket);
                  socket.close();
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
