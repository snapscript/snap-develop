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
   
   @ElementList(entry="value", inline=true)
   private Dictionary<ThemeValue> values;
   
   @Attribute
   private String name;

   public DisplayTheme() {
      this.values = new Dictionary<ThemeValue>();
   }
   
   public String getName(){
      return name;
   }
   
   public TemplateModel getModel() {
      Map<String, Object> variables = new HashMap<String, Object>();
      
      for(ThemeValue value : values) {
         variables.put(value.key, value.value);
      }
      DisplayKey[] keys = DisplayKey.values();
      
      for(DisplayKey key : keys) {
         String name = key.name();
         
         if(!variables.containsKey(name)) {
            throw new IllegalStateException("Theme '" + name + "' does not define '" + name + "'");
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
