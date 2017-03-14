package org.snapscript.develop.http.display;

import java.util.HashMap;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.util.Dictionary;
import org.simpleframework.xml.util.Entry;
import org.snapscript.develop.http.resource.template.TemplateModel;

@Root
public class DisplayTheme {
   
   @ElementList(entry="value", inline=true, required=false)
   private Dictionary<ThemeValue> values;
   
   @Attribute
   private String name;
   
   @Attribute(required=false)
   private String extend;

   public DisplayTheme() {
      this.values = new Dictionary<ThemeValue>();
   }
   
   public String getName(){
      return name;
   }
   
   public TemplateModel getModel(DisplayThemeLoader loader) throws Exception {
      Map<String, Object> variables = new HashMap<String, Object>();
      
      if(extend != null) {
         TemplateModel model = loader.getModel(extend);
         Map<String, Object> base = model.getAttributes();
         
         variables.putAll(base);
      }
      if(values != null) {
         for(ThemeValue value : values) {
            variables.put(value.key, value.value);
         }
      }
      return new TemplateModel(variables);
   }
   
   private static class ThemeValue implements Entry {
      
      @Attribute
      private String key;
      
      @Text
      private String value;

      @Override
      public String getName() {
         return key;
      }
   }
}
