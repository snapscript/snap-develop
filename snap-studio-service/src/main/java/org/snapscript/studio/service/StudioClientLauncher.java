package org.snapscript.studio.service;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import lombok.SneakyThrows;

import org.snapscript.studio.project.HomeDirectory;
import org.snapscript.studio.project.Workspace;
import org.snapscript.ui.ClientContext;
import org.snapscript.ui.ClientControl;
import org.snapscript.ui.ClientEngine;
import org.snapscript.ui.ClientProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StudioClientLauncher {
   
   public static final String CLIENT_LOG = "client.log";
   public static final String CLIENT_CACHE = "cache";         

   private final AtomicReference<ClientControl> reference;
   private final Workspace workspace;
   private final File directory;
   private final boolean disabled;
   private final boolean debug;
   
   public StudioClientLauncher(
         Workspace workspace, 
         @Value("${directory}") File directory, 
         @Value("${server-only}") boolean disabled, 
         @Value("${client-debug}") boolean debug)
   {
      this.reference = new AtomicReference<ClientControl>();
      this.workspace = workspace;
      this.directory = directory;
      this.disabled = disabled;
      this.debug = debug;
   }

   @SneakyThrows
   public void launch(final ClientEngine engine, final String host, final int port) {
      if(!disabled) {
         final File root = HomeDirectory.getRootPath();
         final String path = root.getCanonicalPath();
         final File logFile = HomeDirectory.getPath(CLIENT_LOG);
         final File cachePath = HomeDirectory.getPath(CLIENT_CACHE);
         final String title = directory.getCanonicalPath();
         final ClientContext context = ClientContext.builder()
            .logFile(logFile)
            .cachePath(cachePath)
            .folder(path)
            .debug(debug)            
            .host(host)
            .port(port)            
            .title(title)
            .build();
         
         context.validate();
         
         final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               ClientControl control = ClientProvider.provide(engine).show(context);
               reference.set(control);
            }
         });
         thread.start();
      }
   }

   public void debug() {
      ClientControl control = reference.get();

      if(control != null) {
         control.showDebugger();
      }
   }

}