package org.snapscript.develop.http.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.snapscript.develop.Workspace;

public class RemoteProcessBuilder {
   
   public static final String LAUNCHER_CLASS = "/org/snapscript/develop/http/loader/RemoteProcessLauncher.class";
   public static final String LOADER_CLASS = "/org/snapscript/develop/http/loader/RemoteClassLoader.class";
   public static final String TEMP_PATH = ".temp";
   
   private final ClassResourceLoader loader;
   private final Workspace workspace;
   
   public RemoteProcessBuilder(ClassResourceLoader loader, Workspace workspace) {
      this.workspace = workspace;
      this.loader = loader;
   }
   
   public void create() throws Exception {
      create(LAUNCHER_CLASS);
      create(LOADER_CLASS);
   }
   
   private void create(String path) throws Exception {
      File directory = workspace.create(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, path);
      
      if(file.exists()) {
         file.delete();
      }
      byte[] data = loader.loadClass(path);
      
      if(data == null) {
         throw new IllegalStateException("Could not create launcher " + file);
      }
      File parent = file.getParentFile();
      
      if(!parent.exists()) {
         parent.mkdirs();
      }
      OutputStream stream = new FileOutputStream(file);
      
      try {
         stream.write(data);
      } finally {
         stream.close();
      }
   }
}
