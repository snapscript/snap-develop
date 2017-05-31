
package org.snapscript.develop.resource.loader;

import static org.snapscript.develop.configuration.Configuration.JAR_FILE;
import static org.snapscript.develop.configuration.Configuration.TEMP_PATH;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.snapscript.develop.Workspace;

public class RemoteProcessBuilder {
   
   public static final String LAUNCHER_CLASS = "/org/snapscript/develop/resource/loader/RemoteProcessLauncher.class";
   public static final String LOADER_CLASS = "/org/snapscript/develop/resource/loader/RemoteClassLoader.class";
   public static final String MAIN_CLASS = "org.snapscript.develop.resource.loader.RemoteProcessLauncher";
   public static final String GRAMMAR_FILE = "/grammar.bnf";
   
   private final JarBuilder builder;
   private final Workspace workspace;
   
   public RemoteProcessBuilder(ClassPathResourceLoader loader, Workspace workspace) {
      this.builder = new JarBuilder(loader);
      this.workspace = workspace;
   }
   
   public void create() throws Exception {
      File directory = workspace.create(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);
      
      if(file.exists()) {
         file.delete();
      }
      byte[] data = builder.createJar(MAIN_CLASS, LAUNCHER_CLASS, LOADER_CLASS, GRAMMAR_FILE);
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
