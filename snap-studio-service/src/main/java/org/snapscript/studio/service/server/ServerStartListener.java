package org.snapscript.studio.service.server;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.glassfish.jersey.simple.SimpleServer;
import org.snapscript.studio.common.server.RestServer;
import org.snapscript.studio.service.browser.BrowserLauncher;
import org.snapscript.studio.service.core.CommandLineArgument;
import org.snapscript.studio.service.core.ProcessManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ServerStartListener implements ApplicationListener<ContextRefreshedEvent> {
  
    private final BrowserLauncher launcher;
    private final ProcessManager engine;
    private final RestServer starter;
   
    public void onApplicationEvent(ContextRefreshedEvent event) {
       try {
          ApplicationContext context = event.getApplicationContext();
          SimpleServer server = starter.start(context);
          int port = server.getPort();
          String host = "localhost"; //InetAddress.getLocalHost().getHostName();
          String project = String.format("http://%s:%s/", host, port);
          String script = CommandLineArgument.SCRIPT.getValue();
          String browser = CommandLineArgument.BROWSER_ENGINE.getValue();
          
          log.info("Listening to " + project);
             
          if(script != null) {
             engine.launch(); // start a new process
          }
          launcher.launch(browser, host, port);
          engine.start(host, port);
       } catch(Exception e) {
          throw new IllegalStateException("Could not start server", e);
       }
    }
}