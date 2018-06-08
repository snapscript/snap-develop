package org.snapscript.studio.cli.store;

import java.io.File;
import java.net.URI;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.RemoteStore;
import org.snapscript.common.store.Store;
import org.snapscript.core.module.Path;

public class StoreBuilder {

   private final File path;
   private final String url;
   private final boolean debug;
   private final Path script;
   private Store store;
   
   public StoreBuilder(String url, File path, Path script, boolean debug) {
      this.debug = debug;
      this.script = script;
      this.path = path;
      this.url = url;
   }
   
   public Store create() {
      if(store == null) {
         return createStore();
      }
      return store;
   }
   
   private Store createStore() {
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
   
   private Store createRemoteStore() {
      try {
         String location = url.toLowerCase();
         
         if(!location.startsWith("http:") && !location.startsWith("https:")) {
            throw new IllegalStateException("Resource '" + location + "' is not a url");
         }
         URI file = new URI(url);
         Store delegate = new RemoteStore(file);
         return new FileCacheStore(delegate, script, url, debug);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create store from " + url);
      }
   }
   
   private Store createFileStore() {
      if(!path.exists()) {
         throw new IllegalStateException("Could not create store from " + path);
      }
      return new FileStore(path);
   }
}