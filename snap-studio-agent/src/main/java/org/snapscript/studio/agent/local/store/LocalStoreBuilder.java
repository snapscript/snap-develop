package org.snapscript.studio.agent.local.store;

import java.io.File;
import java.net.URI;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.RemoteStore;
import org.snapscript.common.store.Store;
import org.snapscript.core.module.Path;
import org.snapscript.studio.agent.local.LocalCommandLine;

public class LocalStoreBuilder {
   
   public LocalStoreBuilder() {
      super();
   }
   
   public LocalStore create(LocalCommandLine line) {
      URI url = line.getURI();
      
      try {
         if(url != null) {
            return createRemoteStore(line); 
         } else {
            return createFileStore(line);
         }
      }catch(Exception e) {
         return createFileStore(line);
      }
   }
   
   private LocalStore createRemoteStore(LocalCommandLine line) {
      URI file = line.getURI();
      Path script = line.getScript();
      boolean debug = line.isDebug();
      
      try {
         Store delegate = new RemoteStore(file);
         Store store = new LocalCacheStore(delegate, script, file, debug);
      
         return new LocalStore(store);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create store from " + file);
      }
   }
   
   private LocalStore createFileStore(LocalCommandLine line) {
      File path = line.getDirectory();
      
      if(!path.exists()) {
         throw new IllegalStateException("Could not create store from " + path);
      }
      Store store = new FileStore(path);
      return new LocalStore(store);
   }
}