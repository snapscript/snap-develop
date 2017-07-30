
package org.snapscript.develop.resource.loader;

import static org.snapscript.develop.configuration.Configuration.JAR_FILE;
import static org.snapscript.develop.configuration.Configuration.TEMP_PATH;

import java.io.File;

import org.snapscript.develop.Workspace;

public class RemoteProcessBuilder {

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

      builder.create(RemoteProcessLauncher.class)
               .addResource(RemoteClassLoader.class)
               .addResource(GRAMMAR_FILE)
               .saveFile(file);
   }
}