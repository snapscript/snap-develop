package org.snapscript.studio.resource.loader;

import static org.snapscript.core.Reserved.GRAMMAR_FILE;
import static org.snapscript.core.Reserved.IMPORT_FILE;
import static org.snapscript.studio.configuration.WorkspaceConfiguration.JAR_FILE;
import static org.snapscript.studio.configuration.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;

import javax.annotation.PostConstruct;

import org.snapscript.studio.core.Workspace;
import org.springframework.stereotype.Component;

@Component
public class RemoteProcessBuilder {
   
   private final JarFileBuilder builder;
   private final Workspace workspace;
   
   public RemoteProcessBuilder(ClassPathResourceLoader loader, Workspace workspace) {
      this.builder = new JarFileBuilder(loader);
      this.workspace = workspace;
   }
   
   @PostConstruct
   public void create() throws Exception {
      File directory = workspace.createFile(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);

      builder.create(RemoteProcessLauncher.class)
               .addResource(RemoteClassLoader.class)
               .addResource("/" + GRAMMAR_FILE)
               .addResource("/" + IMPORT_FILE)
               .saveFile(file);
   }
}