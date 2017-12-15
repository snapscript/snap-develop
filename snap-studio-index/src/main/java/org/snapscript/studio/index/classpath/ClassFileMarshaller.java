package org.snapscript.studio.index.classpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

public class ClassFileMarshaller {
   
   private static final String LIBRARY_PATH = "libraryPath";
   private static final String LIBRARY = "library";
   private static final String RESOURCE = "resource";
   private static final String FULL_NAME = "class";
   private static final String TYPE_NAME = "name";
   private static final String CATEGORY = "category";
   private static final String ORIGIN = "origin";
   private static final String MODIFIERS = "modifiers";
   private static final String SHORT_NAME = "shortName";
   private static final String MODULE = "module";

   public static Map<String, String> toAttributes(ClassFile file) {
      Map<String, String> map = new HashMap<String, String>();
    
      String shortName = file.getShortName();
      String libraryPath = file.getLibraryPath();
      String resource = file.getResource();
      String library = file.getLibrary();
      String fullName = file.getFullName();
      String typeName = file.getTypeName();
      String module = file.getModule();
      ClassCategory type = file.getCategory();
      ClassOrigin origin = file.getOrigin();
      int modifiers = file.getModifiers();
      
      Preconditions.checkNotNull(libraryPath, "Attribute '" + LIBRARY_PATH + "' does not exist");
      Preconditions.checkNotNull(resource, "Attribute '" + RESOURCE + "' does not exist");
      Preconditions.checkNotNull(library, "Attribute '" + LIBRARY + "' does not exist");
      Preconditions.checkNotNull(fullName, "Attribute '" + FULL_NAME + "' does not exist");
      Preconditions.checkNotNull(typeName, "Attribute '" + TYPE_NAME + "' does not exist");
      Preconditions.checkNotNull(module, "Attribute '" + MODULE + "' does not exist");
      Preconditions.checkNotNull(type, "Attribute '" + CATEGORY + "' does not exist");
      Preconditions.checkNotNull(origin, "Attribute '" + ORIGIN + "' does not");
      Preconditions.checkNotNull(shortName, "Attribute '" + SHORT_NAME + "' does not exist");
      
      map.put(LIBRARY_PATH, libraryPath);
      map.put(RESOURCE, resource);
      map.put(LIBRARY, library);
      map.put(FULL_NAME, fullName);
      map.put(TYPE_NAME, typeName);
      map.put(CATEGORY, type.name());
      map.put(ORIGIN, origin.name());
      map.put(MODIFIERS, String.valueOf(modifiers));
      map.put(SHORT_NAME, shortName);
      map.put(MODULE, module);
      
      return Collections.unmodifiableMap(map);
   }
   
   public static ClassFile fromAttributes(Map<String, String> map, ClassLoader loader) {
      String shortName = map.get(SHORT_NAME);
      String libraryPath = map.get(LIBRARY_PATH);
      String resource = map.get(RESOURCE);
      String library = map.get(LIBRARY);
      String fullName = map.get(FULL_NAME);
      String typeName = map.get(TYPE_NAME);
      String module = map.get(MODULE);
      String category = map.get(CATEGORY);
      String origin = map.get(ORIGIN);
      String modifiers = map.get(MODIFIERS);
      
      Preconditions.checkNotNull(libraryPath, "Attribute '" + LIBRARY_PATH + "' does not exist for: " + map);
      Preconditions.checkNotNull(resource, "Attribute '" + RESOURCE + "' does not exist for: " + map);
      Preconditions.checkNotNull(library, "Attribute '" + LIBRARY + "' does not exist for: " + map);
      Preconditions.checkNotNull(fullName, "Attribute '" + FULL_NAME + "' does not exist for: " + map);
      Preconditions.checkNotNull(typeName, "Attribute '" + TYPE_NAME + "' does not exist for: " + map);
      Preconditions.checkNotNull(module, "Attribute '" + MODULE + "' does not exist for: " + map);
      Preconditions.checkNotNull(category, "Attribute '" + CATEGORY + "' does not exist for: " + map);
      Preconditions.checkNotNull(origin, "Attribute '" + ORIGIN + "' does not exist for: " + map);
      Preconditions.checkNotNull(shortName, "Attribute '" + SHORT_NAME + "' does not exist for: " + map);
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
      public ClassCategory getCategory() {
         String type = getAttribute(CATEGORY);
         return ClassCategory.valueOf(type);
      }

      @Override
      public ClassOrigin getOrigin() {
         String category = getAttribute(ORIGIN);
         return ClassOrigin.valueOf(category);
      }

      @Override
      public int getModifiers() {
         String modifiers = getAttribute(MODIFIERS);
         return Integer.parseInt(modifiers);
      }

      @Override
      public String getLibraryPath() {
         return getAttribute(LIBRARY_PATH);
      }

      @Override
      public String getResource() {
         return getAttribute(RESOURCE);
      }

      @Override
      public String getLibrary() {
         return getAttribute(LIBRARY);
      }

      @Override
      public String getFullName() {
         return getAttribute(FULL_NAME);
      }

      @Override
      public String getTypeName() {
         return getAttribute(TYPE_NAME);
      }

      @Override
      public String getShortName() {
         return getAttribute(SHORT_NAME);
      }

      @Override
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
