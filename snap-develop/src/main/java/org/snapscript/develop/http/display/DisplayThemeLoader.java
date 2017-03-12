package org.snapscript.develop.http.display;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.xml.core.Persister;
import org.snapscript.develop.http.loader.ClassResourceLoader;
import org.snapscript.develop.http.resource.template.TemplateModel;

public class DisplayThemeLoader {

   private final Map<String, DisplayTheme> themes;
   private final Persister persister;
   private final List<String> sources;
   
   public DisplayThemeLoader(List<String> sources) {
      this.themes = new ConcurrentHashMap<String, DisplayTheme>();
      this.persister = new Persister();
      this.sources = sources;
   }
   
   public void validateThemes() throws Exception {
      for(String source : sources) {
         byte[] data = ClassResourceLoader.loadResource(source);
         InputStream stream = new ByteArrayInputStream(data);
         DisplayTheme theme = persister.read(DisplayTheme.class, stream);
         String name = theme.getName();
         
         themes.put(name,  theme);
      }
   }
   
   public DisplayTheme getTheme(String name) {
      DisplayTheme theme = themes.get(name);
      
      if(theme == null) {
         throw new IllegalStateException("Could not find " + name);
      }
      return theme;
   }
   
   public TemplateModel getModel(String name) {
      return getTheme(name).getModel();
   }
}
