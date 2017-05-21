package org.snapscript.develop.resource.template;

import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.develop.resource.FileResolver;

public class StringTemplateEngine implements TemplateEngine {
   
   private final Cache<String, Template> cache;
   private final TemplateFinder finder;
   private final PropertyBinder binder;
   
   public StringTemplateEngine(FileResolver resolver, String prefix) {
      this(resolver, prefix, 0); // default is no caching
   }
   
   public StringTemplateEngine(FileResolver resolver, String prefix, int capacity) {
      this.cache = new LeastRecentlyUsedCache<String, Template>(capacity);
      this.finder = new TemplateFinder(resolver, prefix);
      this.binder = new PropertyBinder();
   }   

   @Override
   public String renderTemplate(TemplateModel model, String source) throws Exception {      
      TemplateFilter filter = createFilter(model);
      Template template = resolveTemplate(source);
      
      if(template != null) {
         try {
            StringWriter writer = new StringWriter();
            
            if(template != null) {
               template.render(filter, writer);
            }
            return writer.toString();
         } catch(Exception e) {
            throw new IllegalStateException("Could not render template '" + source + "'", e);
         }
      }
      return null;
   }   
   
   @Override
   public boolean validTemplate(String path) throws Exception {
      String file = finder.findPath(path);

      if (file != null) {
         return true;
      }
      return false;
   }   
   
   protected Template resolveTemplate(String source) throws Exception {
      Template template = cache.fetch(source);
      
      if(template == null) {
         template = createTemplate(source);
         cache.cache(source, template);
      }
      return template;
   }
   
   protected Template createTemplate(String source) throws Exception {
      Reader reader = finder.findReader(source);
      
      if(reader != null) {
         try {
            StringBuilder builder = new StringBuilder();
            LineNumberReader iterator = new LineNumberReader(reader);
            
            while(iterator.ready()) {
               String line = iterator.readLine();
               
               if(line == null) {
                  break;
               }
               builder.append(line);
               builder.append("\n");
            }
            String text = builder.toString();
            return new StringTemplate(source, text);
         }finally {
            reader.close();
         }
      }
      return null;
   }
   
   protected TemplateFilter createFilter(TemplateModel model) throws Exception {
      return new PropertyTemplateFilter(model, binder);
   }
}

