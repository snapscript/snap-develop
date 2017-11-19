package org.snapscript.studio.common.resource.display;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.xml.core.Persister;
import org.snapscript.core.Bug;
import org.snapscript.studio.common.FileDirectorySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DisplayPersister {
   
   private final AtomicReference<DisplayFile> reference;
   private final FileDirectorySource workspace;
   private final Persister persister;
   private final String theme;
   
   @Bug("this is rubbish")
   public DisplayPersister(FileDirectorySource workspace, @Value("${theme.file:.display}") String theme) {
      this.reference = new AtomicReference<DisplayFile>();
      this.persister = new Persister();
      this.workspace = workspace;
      this.theme = theme;
   }
   
   public synchronized DisplayDefinition readDefinition(){
      return getDisplayFile().readDefinition();
   }
   
   public synchronized void saveDefinition(DisplayDefinition definition) {
      getDisplayFile().saveDefinition(definition);
   }
   
   private synchronized DisplayFile getDisplayFile() {
      DisplayFile displayFile = reference.get();
      
      if(displayFile == null) {
         File file = workspace.createFile(theme);
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
            workspace.getLogger().info("Could not save display", e);
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
            workspace.getLogger().info("Could not read theme", e);
         }
         if(definition == null) {
            return DisplayDefinition.getDefault();
         }
         return definition;
      }
   }
}