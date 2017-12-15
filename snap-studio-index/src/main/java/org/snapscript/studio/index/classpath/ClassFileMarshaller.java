package org.snapscript.studio.index.classpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class ClassFileMarshaller {
   
   private static final String ABSOLUTE_PATH = "absolutePath";
   private static final String RESOURCE_NAME = "resourceName";
   private static final String LOCATION = "location";
   private static final String FULL_NAME = "fullName";
   private static final String TYPE_NAME = "typeName";
   private static final String CLASS_TYPE = "classType";
   private static final String CLASS_CATEGORY = "classCategory";
   private static final String MODIFIERS = "classModifiers";
   private static final String NAME = "name";
   private static final String MODULE = "module";

   public static Map<String, String> toAttributes(ClassFile file) {
      Map<String, String> map = new HashMap<String, String>();
    
      String name = file.getName();
      String absolutePath = file.getAbsolutePath();
      String resourceName = file.getResourceName();
      String location = file.getLocation();
      String fullName = file.getFullName();
      String typeName = file.getTypeName();
      String module = file.getModule();
      ClassFileType type = file.getClassType();
      ClassFileCategory category = file.getClassCategory();
      int modifiers = file.getModifiers();
      
      Preconditions.checkNotNull(absolutePath, "Attribute '" + ABSOLUTE_PATH + "' does not exist");
      Preconditions.checkNotNull(resourceName, "Attribute '" + RESOURCE_NAME + "' does not exist");
      Preconditions.checkNotNull(location, "Attribute '" + LOCATION + "' does not exist");
      Preconditions.checkNotNull(fullName, "Attribute '" + FULL_NAME + "' does not exist");
      Preconditions.checkNotNull(typeName, "Attribute '" + TYPE_NAME + "' does not exist");
      Preconditions.checkNotNull(module, "Attribute '" + MODULE + "' does not exist");
      Preconditions.checkNotNull(type, "Attribute '" + CLASS_TYPE + "' does not exist");
      Preconditions.checkNotNull(category, "Attribute '" + CLASS_CATEGORY + "' does not");
      Preconditions.checkNotNull(name, "Attribute '" + NAME + "' does not exist");
      
      map.put(ABSOLUTE_PATH, absolutePath);
      map.put(RESOURCE_NAME, resourceName);
      map.put(LOCATION, location);
      map.put(FULL_NAME, fullName);
      map.put(TYPE_NAME, typeName);
      map.put(CLASS_TYPE, type.name());
      map.put(CLASS_CATEGORY, category.name());
      map.put(MODIFIERS, String.valueOf(modifiers));
      map.put(NAME, name);
      map.put(MODULE, module);
      
      return Collections.unmodifiableMap(map);
   }
   
   public static ClassFile fromAttributes(Map<String, String> map, ClassLoader loader) {
      String name = map.get(NAME);
      String absolutePath = map.get(ABSOLUTE_PATH);
      String resourceName = map.get(RESOURCE_NAME);
      String location = map.get(LOCATION);
      String fullName = map.get(FULL_NAME);
      String typeName = map.get(TYPE_NAME);
      String module = map.get(MODULE);
      String type = map.get(CLASS_TYPE);
      String category = map.get(CLASS_CATEGORY);
      String modifiers = map.get(MODIFIERS);
      
      Preconditions.checkNotNull(absolutePath, "Attribute '" + ABSOLUTE_PATH + "' does not exist for: " + map);
      Preconditions.checkNotNull(resourceName, "Attribute '" + RESOURCE_NAME + "' does not exist for: " + map);
      Preconditions.checkNotNull(location, "Attribute '" + LOCATION + "' does not exist for: " + map);
      Preconditions.checkNotNull(fullName, "Attribute '" + FULL_NAME + "' does not exist for: " + map);
      Preconditions.checkNotNull(typeName, "Attribute '" + TYPE_NAME + "' does not exist for: " + map);
      Preconditions.checkNotNull(module, "Attribute '" + MODULE + "' does not exist for: " + map);
      Preconditions.checkNotNull(type, "Attribute '" + CLASS_TYPE + "' does not exist for: " + map);
      Preconditions.checkNotNull(category, "Attribute '" + CLASS_CATEGORY + "' does not exist for: " + map);
      Preconditions.checkNotNull(name, "Attribute '" + NAME + "' does not exist for: " + map);
      Preconditions.checkNotNull(modifiers, "Attribute '" + MODIFIERS + "' does not exist for: " + map);
      
      return new MapClassFile(map, loader);
   }
   
   private static class MapClassFile implements ClassFile {

      private final Map<String, String> map;
      private final ClassLoader loader;
      private Class type;

      public MapClassFile(Map<String, String> map, ClassLoader loader) {
         this.loader = loader;
         this.map = map;
      }

      @Override
      public ClassFileType getClassType() {
         String type = getAttribute(CLASS_TYPE);
         return ClassFileType.valueOf(type);
      }

      @Override
      public ClassFileCategory getClassCategory() {
         String category = getAttribute(CLASS_CATEGORY);
         return ClassFileCategory.valueOf(category);
      }

      @Override
      public int getModifiers() {
         String modifiers = getAttribute(MODIFIERS);
         return Integer.parseInt(modifiers);
      }

      public String getAbsolutePath() {
         return getAttribute(ABSOLUTE_PATH);
      }

      public String getResourceName() {
         return getAttribute(RESOURCE_NAME);
      }

      public String getLocation() {
         return getAttribute(LOCATION);
      }

      public String getFullName() {
         return getAttribute(FULL_NAME);
      }

      public String getTypeName() {
         return getAttribute(TYPE_NAME);
      }

      public String getName() {
         return getAttribute(NAME);
      }

      public String getModule() {
         return getAttribute(MODULE);
      }
      
      private String getAttribute(String name) {
         String value = map.get(name);
         
         if(value == null) {
            throw new IllegalStateException("Attribute '" + name + "' does not exist for: " + map);
         }
         return value;
      }

      @Override
      public Class loadClass() {
         try {
            if (type == null) {
               String path = getFullName();
               type = loader.loadClass(path);
            }
         } catch (Throwable e) {
            return null;
         }
         return type;
      }
   }
}
