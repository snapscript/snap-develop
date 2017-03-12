package org.snapscript.develop.http.display;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.simpleframework.xml.core.Persister;
import org.snapscript.develop.Workspace;
import org.snapscript.develop.http.resource.template.TemplateModel;

public class DisplayModelResolver {
   
   private final AtomicReference<DisplayFile> reference;
   private final DisplayThemeLoader loader;
   private final Workspace workspace;
   private final Persister persister;
   private final String theme;
   
   public DisplayModelResolver(DisplayThemeLoader loader, Workspace workspace, String theme) {
      this.reference = new AtomicReference<DisplayFile>();
      this.persister = new Persister();
      this.workspace = workspace;
      this.loader = loader;
      this.theme = theme;
   }

   public TemplateModel getModel() throws Exception {
      DisplayFile displayFile = reference.get();
      
      if(displayFile == null) {
         File file = workspace.create(theme);
         displayFile = new DisplayFile(file);
         reference.set(displayFile);
      }
      String theme = displayFile.getThemeName();
      TemplateModel model = loader.getModel(theme);
      Map<String, Object> variables = model.getAttributes();
      Map<String, Object> copy = new HashMap<String, Object>(variables);
      return new TemplateModel(copy);
   }
   
   private class DisplayFile {
      
      private File displayFile;
      private String themeName;
      private long loadTime;
      
      public DisplayFile(File displayFile) {
         this.displayFile = displayFile;
      }
      
      public String getThemeName() {
         try {
            if(displayFile.exists()) {
               long modifiedTime = displayFile.lastModified();
               
               if(themeName == null || loadTime < modifiedTime) {   
                  DisplayDefinition display = persister.read(DisplayDefinition.class, displayFile);
                  loadTime = modifiedTime;
                  themeName = display.getThemeName();
               }
               return themeName;
            }
         }catch(Exception e) {
            e.printStackTrace();
         }
         DisplayDefinition display = DisplayDefinition.getDefault();
         return display.getThemeName();
      }
   }
}
