package org.snapscript.studio.cli.debug;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.core.module.Path;
import org.snapscript.studio.agent.DebugAgent;
import org.snapscript.studio.agent.DebugClient;
import org.snapscript.studio.agent.DebugContext;
import org.snapscript.studio.agent.log.LogLevel;

public class DebugRequestListener {

   private final DebugRequestMarshaller marshaller;
   private final ConnectLauncher launcher;
   private final DebugContext context;
   private final AtomicBoolean active;
   private final Path script;
   
   public DebugRequestListener(DebugContext context, Path script, int port) {
      this.launcher = new ConnectLauncher(context, this, port);
      this.marshaller = new DebugRequestMarshaller();
      this.active = new AtomicBoolean();
      this.context = context;
      this.script = script;
   }
   
   public void debug(DebugRequest request) {
      DebugAgent agent = new DebugAgent(context, LogLevel.INFO.name());
      
      try {
         String project = request.getProject();
         URI root = request.getTarget();
         DebugClient client = agent.start(root, launcher);
         String path = script.getPath();
         
         client.attachProcess(project, path);
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
      
      private final DebugRequestListener acceptor;
      private final int port;
      
      public ConnectListener(DebugRequestListener acceptor, int port) {
         this.acceptor = acceptor;
         this.port = port;
      }
      
      public void run() {
         try {
            ServerSocket listener = new ServerSocket(port);
            Socket socket = listener.accept();
            DebugRequest target = marshaller.readRequest(socket);
            
            active.set(false);
            listener.close();
            acceptor.debug(target);
         } catch(Exception e){
            active.set(false);
            e.printStackTrace();
         }
         
      }
   }
}
