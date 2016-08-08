package org.snapscript.develop.http.resource.template;

import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.snapscript.develop.http.resource.FileResolver;

public class VelocityTemplateEngine implements TemplateEngine {

   private final VelocityEngine engine;
   private final TemplateFinder finder;

   public VelocityTemplateEngine(VelocityEngine engine, FileResolver resolver, String prefix, String suffix) {
      this.finder = new TemplateFinder(resolver, prefix, suffix);
      this.engine = engine;
   }

   @Override
   public String renderTemplate(TemplateModel model, String path) throws Exception {
      Reader reader = finder.findReader(path);

      if (reader != null) {
         VelocityConverter converter = new VelocityConverter(model);
         VelocityContext internal = new VelocityContext();         
         StringWriter writer = new StringWriter();

         try {
            Map<String, Object> attributes = model.getAttributes();
            Set<String> keys = attributes.keySet();

            for (String key : keys) {
               Object value = attributes.get(key);

               value = converter.convert(value);
               internal.put(key, value);
            }            
            engine.evaluate(internal, writer, path, reader);
         } finally {
            reader.close();
         }
         return writer.toString();
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
}
