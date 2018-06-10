package org.snapscript.studio.cli.store;

import java.io.InputStream;
import java.io.OutputStream;

import org.snapscript.common.store.Store;
import org.snapscript.studio.agent.ProjectStore;

public class ProcessStore implements ProjectStore {
   
   private final Store store;
   
   public ProcessStore(Store store) {
      this.store = store;
   }

   @Override
   public InputStream getInputStream(String path) {
      return store.getInputStream(path);
   }

   @Override
   public OutputStream getOutputStream(String path) {
      return store.getOutputStream(path);
   }

   @Override
   public void update(String project) {}
}
