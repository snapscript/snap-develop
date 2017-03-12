package org.snapscript.develop.http.display;

import java.util.HashMap;
import java.util.Map;

import org.snapscript.develop.http.resource.template.TemplateModel;

public class DisplayModelResolver {
   
   private final DisplayPersister persister;
   private final DisplayThemeLoader loader;
   
   public DisplayModelResolver(DisplayPersister persister, DisplayThemeLoader loader) {
      this.persister = persister;
      this.loader = loader;
   }
   
   public TemplateModel getModel() throws Exception {
      return getModel(null);
   }

   public TemplateModel getModel(String name) throws Exception {
      String theme = getTheme(name);
      TemplateModel model = loader.getModel(theme);
      Map<String, Object> variables = model.getAttributes();
      Map<String, Object> copy = new HashMap<String, Object>(variables);
      return new TemplateModel(copy);
   }
   
   private String getTheme(String theme) throws Exception {
      if(theme == null) {
         DisplayDefinition definition = persister.readDefinition();
         return definition.getThemeName();
      }
      return theme;
   }
}
