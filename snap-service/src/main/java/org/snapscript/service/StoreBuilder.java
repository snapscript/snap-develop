package org.snapscript.service;

import java.io.File;
import java.net.URI;

import org.snapscript.common.store.FileStore;
import org.snapscript.common.store.RemoteStore;
import org.snapscript.common.store.Store;
import org.snapscript.core.Path;

public class StoreBuilder {

   private Path script;
   private Store store;
   private String root;
   
   public StoreBuilder(String root, Path script) {
      this.script = script;
      this.root = root;
   }
   
   public Store create() {
      if(store == null) {
         return createStore();
      }
      return store;
   }
   
   private Store createStore() {
      try {
         return createRemoteStore(); 
      }catch(Exception e) {
         return createFileStore();
      }
   }
   
   private Store createRemoteStore() {
      try {
         String location = root.toLowerCase();
         
         if(!location.startsWith("http:") && !location.startsWith("https:")) {
            throw new IllegalStateException("Resource '" + location + "' is not a url");
         }
         URI file = new URI(root);
         Store delegate = new RemoteStore(file);
         return new FileCacheStore(delegate, script, root);
      } catch(Exception e) {
         throw new IllegalStateException("Could not create store from " + root);
      }
   }
   
   private Store createFileStore() {
      File file = new File(root);
      
      if(!file.exists()) {
         throw new IllegalStateException("Could not create store from " + root);
      }
      return new FileStore(file);
   }
}