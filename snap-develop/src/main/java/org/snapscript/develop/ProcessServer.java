
package org.snapscript.develop;

import java.net.InetSocketAddress;

import org.snapscript.develop.browser.BrowserLauncher;
import org.snapscript.develop.resource.WebServer;

public class ProcessServer {

   private final BrowserLauncher launcher;
   private final ProcessManager engine;
   private final WebServer server;
   
   public ProcessServer(ProcessManager engine, BrowserLauncher launcher, WebServer server) {
      this.launcher = launcher;
      this.engine = engine;
      this.server = server;
   }
   
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
