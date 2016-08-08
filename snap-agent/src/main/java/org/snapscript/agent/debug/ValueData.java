package org.snapscript.agent.debug;

import java.util.LinkedHashMap;
import java.util.Map;

public class ValueData {
   
   public static final String NAME_KEY = "name";
   public static final String TYPE_KEY = "type";
   public static final String VALUE_KEY = "value";
   public static final String DESCRIPTION_KEY = "description";
   public static final String EXPANDABLE_KEY = "expandable";
   public static final String DEPTH_KEY = "depth";

   private final String name;
   private final String type;
   private final String value;
   private final String description;
   private final boolean expandable;
   private final int depth;
   
   public ValueData(String name, String type, String value, String description, boolean expandable, int depth) {
      this.name = name;
      this.type = type;
      this.value = value;
      this.description = description;
      this.expandable = expandable;
      this.depth = depth;
   }
   
   public Map<String, String> getData() {
      Map<String, String> data = new LinkedHashMap<String, String>();
      data.put(NAME_KEY, name);
      data.put(TYPE_KEY, type);
      data.put(VALUE_KEY, value);
      data.put(DESCRIPTION_KEY, description);
      data.put(EXPANDABLE_KEY, String.valueOf(expandable));
      data.put(DEPTH_KEY, String.valueOf(depth));
      return data;
   }
   
   
}
