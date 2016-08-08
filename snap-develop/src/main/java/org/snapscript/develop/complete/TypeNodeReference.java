package org.snapscript.develop.complete;

public class TypeNodeReference {

   public static final String CLASS = "class";
   public static final String MODULE = "module";
   
   private final String name;
   private final String resource;
   private final String type;
   
   public TypeNodeReference(String name, String resource, String type) {
      this.name = name;
      this.resource = resource;
      this.type = type;
   }

   public String getName() {
      return name;
   }

   public String getResource() {
      return resource;
   }

   public String getType() {
      return type;
   }
}
