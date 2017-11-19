package org.snapscript.studio.core;

import java.net.InetSocketAddress;

import javax.annotation.PostConstruct;

import lombok.AllArgsConstructor;

import org.snapscript.studio.browser.BrowserLauncher;
import org.snapscript.studio.common.resource.WebServer;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProcessServer {

   private final BrowserLauncher launcher;
   private final ProcessManager engine;
   private final WebServer server;
   
   @PostConstruct
   public void start() {
      try {
         InetSocketAddress address = server.start();
         int port = address.getPort();
         String host = address.getHostName();
         String project = String.format("http://%s:%s/", host, port);
         String script = CommandLineArgument.SCRIPT.getValue();
         String browser = CommandLineArgument.BROWSER_ENGINE.getValue();
            
         if(script != null) {
            engine.launch(); // start a new process
         }
         System.err.println(project);
         launcher.launch(browser, host, port);
         engine.start(host, port);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}