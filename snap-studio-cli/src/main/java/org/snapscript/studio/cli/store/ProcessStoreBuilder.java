package org.snapscript.studio.cli.store;

import java.io.File;
import java.net.URI;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.RemoteStore;
import org.snapscript.common.store.Store;
import org.snapscript.core.module.Path;

public class ProcessStoreBuilder {

   private final File path;
   private final String url;
   private final boolean debug;
   private final Path script;
   private ProcessStore store;
   
   public ProcessStoreBuilder(String url, File path, Path script, boolean debug) {
      this.debug = debug;
      this.script = script;
      this.path = path;
      this.url = url;
   }
   
   public ProcessStore create() {
      if(store == null) {
         return createStore();
      }
      return store;
   }
   
   private ProcessStore createStore() {
      try {
         if(url != null) {
            return createRemoteStore(); 
         } else {
            return createFileStore();
         }
      }catch(Exception e) {
         return createFileStore();
      }
   }
   
   private ProcessStore createRemoteStore() {
      try {
         String location = url.toLowerCase();
         
         if(!location.startsWith("http:") && !location.startsWith("https:")) {
            throw new IllegalStateException("Resource '" + location + "' is not a url");
         }
         URI file = new URI(url);
         Store delegate = new RemoteStore(file);
         Store store = new FileCacheStore(delegate, script, url, debug);
         return new ProcessStore(store);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create store from " + url);
      }
   }
   
   private ProcessStore createFileStore() {
      if(!path.exists()) {
         throw new IllegalStateException("Could not create store from " + path);
      }
      Store store = new FileStore(path);
      return new ProcessStore(store);
   }
}