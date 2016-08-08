package org.snapscript.develop;

import org.snapscript.develop.http.WebServer;

public class ProcessServer {

   private final ProcessManager engine;
   private final WebServer server;
   
   public ProcessServer(ProcessManager engine, WebServer server) {
      this.engine = engine;
      this.server = server;
   }
   
   public void start() {
      try {
         int port = server.start();
         String project = String.format("http://localhost:%s/", port);
         String script = CommandLineArgument.SCRIPT.getValue();
            
         if(script != null) {
            engine.launch(); // start a new process
         }
         System.err.println(project);
         engine.start(port);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}
