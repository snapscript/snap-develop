package org.snapscript.studio.service.loader;

import static org.snapscript.core.Reserved.GRAMMAR_FILE;
import static org.snapscript.core.Reserved.IMPORT_FILE;
import static org.snapscript.studio.project.config.WorkspaceConfiguration.JAR_FILE;
import static org.snapscript.studio.project.config.WorkspaceConfiguration.TEMP_PATH;

import java.io.File;

import javax.annotation.PostConstruct;

import org.snapscript.studio.project.HomeDirectory;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
public class SandBoxBuilder {
   
   private final JarFileBuilder builder;
   private final Workspace workspace;
   
   public SandBoxBuilder(ClassPathResourceLoader loader, Workspace workspace) {
      this.builder = new JarFileBuilder(loader);
      this.workspace = workspace;
   }
   
   @PostConstruct
   public void create() throws Exception {
      File directory = HomeDirectory.getPath(TEMP_PATH);
      
      if(!directory.exists()) {
         directory.mkdirs();
      }
      File file = new File(directory, JAR_FILE);

      builder.create(SandBoxLauncher.class)
               .addResource(SandBoxClassLoader.class)
               .addResource("/" + GRAMMAR_FILE)
               .addResource("/" + IMPORT_FILE)
               .saveFile(file);
   }
}