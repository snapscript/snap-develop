
package org.snapscript.develop.resource.loader;

import static org.snapscript.develop.configuration.Configuration.JAR_FILE;
import static org.snapscript.develop.configuration.Configuration.TEMP_PATH;

import java.io.File;

import org.snapscript.develop.Workspace;

public class RemoteProcessBuilder {
   
   public static final String LAUNCHER_CLASS = "/org/snapscript/develop/resource/loader/RemoteProcessLauncher.class";
   public static final String LOADER_CLASS = "/org/snapscript/develop/resource/loader/RemoteClassLoader.class";
   public static final String MAIN_CLASS = "org.snapscript.develop.resource.loader.RemoteProcessLauncher";
   public static final String GRAMMAR_FILE = "/grammar.bnf";
   
   private final JarFileBuilder builder;
   private final Workspace workspace;
   
   public RemoteProcessBuilder(ClassPathResourceLoader loader, Workspace workspace) {
      this.builder = new JarFileBuilder(loader);
      this.workspace = workspace;
   }
   
   public void create() throws Exception {
      File directory = workspace.create(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);

      builder.create(MAIN_CLASS)
               .addResource(LAUNCHER_CLASS)
               .addResource(LOADER_CLASS)
               .addResource(GRAMMAR_FILE)
               .saveFile(file);
   }
}
