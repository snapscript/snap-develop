package org.snapscript.studio.agent.local;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.common.thread.ThreadBuilder;
import org.snapscript.core.module.Path;
import org.snapscript.studio.agent.ProcessAgent;
import org.snapscript.studio.agent.ProcessClient;
import org.snapscript.studio.agent.ProcessContext;
import org.snapscript.studio.agent.local.message.AttachRequest;
import org.snapscript.studio.agent.local.message.DetachRequest;
import org.snapscript.studio.agent.log.LogLevel;

public class LocalProcessController {

   private final AtomicReference<ProcessClient> reference;
   private final ConnectLauncher launcher;
   private final ProcessContext context;
   private final AtomicBoolean active;
   private final Path script;
   
   public LocalProcessController(ProcessContext context, Path script, int port) {
      this.launcher = new ConnectLauncher(context, this, port);
      this.reference = new AtomicReference<ProcessClient>();
      this.active = new AtomicBoolean();
      this.context = context;
      this.script = script;
   }

   public String attachRequest(AttachRequest request) {
      String project = request.getProject();
      URI root = request.getTarget();
      String path = script.getPath();
      
      if(!isAttached()) {
         try {
            ProcessAgent agent = new ProcessAgent(context, LogLevel.INFO);
            ProcessClient client = agent.start(root, launcher);
            
            reference.set(client);
            client.attachProcess(project, path);
         }catch(Exception e){
            e.printStackTrace();
         }
      }
      return context.getProcess();
   }
   
   public String detachRequest(DetachRequest request) {
      ProcessClient client = reference.getAndSet(null);
      
      try {
         if(client != null) {
            client.detachClient();
         }
      }catch(Exception e){
         e.printStackTrace();
      }
      return context.getProcess(); 
   }
   
   public void reset() {
      ProcessClient client = reference.getAndSet(null);
      
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
      
      private final LocalProcessController listener;
      private final ConnectAcceptor acceptor;
      private final ThreadFactory factory;
      
      public ConnectLauncher(ProcessContext context, LocalProcessController listener, int port) {
         this.acceptor = new ConnectAcceptor(listener, port);
         this.factory = new ThreadBuilder();
         this.listener = listener;
      }

      @Override
      public void run() {
         listener.reset();
         
         if(active.compareAndSet(false, true)) {
            Thread thread = factory.newThread(acceptor);
            thread.start();
         }
      }      
   }
   
   private class ConnectAcceptor implements Runnable {
      
      private final LocalMessageConsumer consumer;
      private final int port;
      
      public ConnectAcceptor(LocalProcessController listener, int port) {
         this.consumer = new LocalMessageConsumer(listener);
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
