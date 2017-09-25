package org.snapscript.studio;

import java.net.InetSocketAddress;

import lombok.AllArgsConstructor;

import org.snapscript.studio.browser.BrowserLauncher;
import org.snapscript.studio.resource.WebServer;

@AllArgsConstructor
public class ProcessServer {

   private final BrowserLauncher launcher;
   private final ProcessManager engine;
   private final WebServer server;
   
   public void start() {
      try {
         InetSocketAddress address = server.start();
         int port = address.getPort();
         String host = address.getHostName();
         String project = String.format("http://%s:%s/", host, port);
         String script = CommandLineArgument.SCRIPT.getValue();
            
         if(script != null) {
            engine.launch(); // start a new process
         }
         System.err.println(project);
         launcher.launch(host, port);
         engine.start(host, port);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}