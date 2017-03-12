package org.snapscript.develop.http.display;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.xml.core.Persister;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.Workspace;

public class DisplayPersister {
   
   private final AtomicReference<DisplayFile> reference;
   private final ProcessLogger logger;
   private final Workspace workspace;
   private final Persister persister;
   private final String theme;
   
   public DisplayPersister(ProcessLogger logger, Workspace workspace, String theme) {
      this.reference = new AtomicReference<DisplayFile>();
      this.persister = new Persister();
      this.workspace = workspace;
      this.logger = logger;
      this.theme = theme;
   }
   
   public DisplayDefinition readDefinition(){
      return getDisplayFile().readDefinition();
   }
   
   public void saveDefinition(DisplayDefinition definition) {
      getDisplayFile().saveDefinition(definition);
   }
   
   private DisplayFile getDisplayFile() {
      DisplayFile displayFile = reference.get();
      
      if(displayFile == null) {
         File file = workspace.create(theme);
         displayFile = new DisplayFile(file);
         reference.set(displayFile);
      }
      return displayFile;
   }

   private class DisplayFile {
      
      private AtomicReference<DisplayDefinition> reference;
      private File displayFile;
      private long loadTime;
      
      public DisplayFile(File displayFile) {
         this.reference = new AtomicReference<DisplayDefinition>();
         this.displayFile = displayFile;
      }
      
      public void saveDefinition(DisplayDefinition definition) {
         try {
            if(displayFile.exists()) {
               persister.write(definition, displayFile);
               loadTime = displayFile.lastModified();
            }
         }catch(Exception e) {
            logger.info("Could not save display", e);
         }
         reference.set(definition);
      }
      
      public DisplayDefinition readDefinition() {            
         DisplayDefinition definition = reference.get();
      
         try {
            if(displayFile.exists()) {
               long modifiedTime = displayFile.lastModified();
               
               if(definition == null || loadTime < modifiedTime) {   
                  definition = persister.read(DisplayDefinition.class, displayFile);
                  loadTime = modifiedTime;
                  reference.set(definition);
               }
            }
         }catch(Exception e) {
            logger.info("Could not read theme", e);
         }
         if(definition == null) {
            return DisplayDefinition.getDefault();
         }
         return definition;
      }
   }
}
