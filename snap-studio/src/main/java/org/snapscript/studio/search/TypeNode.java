package org.snapscript.studio.search;

import static org.snapscript.core.Reserved.SCRIPT_EXTENSION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.snapscript.core.Context;
import org.snapscript.core.Module;
import org.snapscript.core.ResourceManager;
import org.snapscript.core.Type;
import org.snapscript.core.define.SuperExtractor;
import org.snapscript.core.function.Function;
import org.snapscript.core.property.Property;

public class TypeNode {
   
   public static TypeNode createNode(Context context, Class require, String key) {
      Type type = context.getLoader().loadType(require);
      String fullName = require.getCanonicalName();
      String typePath = createFullPath(fullName) + ".java";
      
      return new TypeNode(type, typePath, key);
   }
   
   public static TypeNode createNode(Context context, Type type, String key) {
      ResourceManager manager = context.getManager();
      Set<String> possiblePaths = createPossiblePaths(type);
      String path = type.getModule().getPath().getPath();
      
      for(String possiblePath : possiblePaths) {
         String text = manager.getString(possiblePath);
         
         if(text != null){
            return new TypeNode(type, possiblePath, key);
         }
      }
      return new TypeNode(type, path, key);
   }
   
   public static TypeNode createNode(Context context, Module module, String key) {
      ResourceManager manager = context.getManager();
      Set<String> possiblePaths = createPossiblePaths(module);
      String path = module.getPath().getPath();
      
      for(String possiblePath : possiblePaths) {
         String text = manager.getString(possiblePath);
         
         if(text != null){
            return new TypeNode(module, possiblePath, key);
         }
      }
      return new TypeNode(module, path, key);
   }
   
   private static String createFullPath(String path) {
      if(!path.startsWith("/")) {
         path = "/" + path;
      }
      if(path.endsWith(".class")) {
         path = path.substring(0, path.lastIndexOf(".class"));
      }
      if(path.contains("$")) {
         return path.substring(0, path.indexOf('$'));
      }
      return path.replace(".", "/");
   }
   
   private static Set<String> createPossiblePaths(Type type) {
      Set<String> possiblePaths = new LinkedHashSet<String>();
      String possiblePath = "/" + type.toString().replace(".", "/");
      
      possiblePaths.add(possiblePath + SCRIPT_EXTENSION);
      
      if(possiblePath.contains("$")) {
         possiblePaths.add(createFullPath(possiblePath) + SCRIPT_EXTENSION);
      }
      possiblePaths.add(type.getModule().getPath().getPath());
      
      return possiblePaths;
   }
   
   private static Set<String> createPossiblePaths(Module module) {
      Set<String> possiblePaths = new LinkedHashSet<String>();
      String possiblePath = "/" + module.toString().replace(".", "/");
      
      possiblePaths.add(possiblePath + SCRIPT_EXTENSION);
      possiblePaths.add(module.getPath().getPath());
      
      return possiblePaths;
   }

   private final Object value;
   private final String name;
   private final String path;
   
   public TypeNode(Type value, String path, String name) {
      this.value = value;
      this.name = name;
      this.path = path;
   }
   
   public TypeNode(Module value, String path, String name) {
      this.value = value;
      this.name = name;
      this.path = path;
   }
   
   public String getName() {
      return name;
   }
   
   public Class getType() {
      if(Type.class.isInstance(value)) {
         return ((Type)value).getType();
      }
      return null;
   }
   
   public List<Function> getFunctions(){
      return getFunctions(true);
   }
   
   public List<Function> getFunctions(boolean includeSuper){
      if(Module.class.isInstance(value)) {
         return ((Module)value).getFunctions();
      }
      if(Type.class.isInstance(value)) {
         List<Function> total = new ArrayList<Function>();
         SuperExtractor extractor = new SuperExtractor();
         Type type = (Type)value;
         
         while(type != null) {
            List<Function> functions = type.getFunctions();
            total.addAll(functions);
            
            if(!includeSuper) {
               break;
            }
            type = extractor.extractor(type);
         }
         return total;
      }
      return Collections.emptyList();
   }
   
   public List<Property> getProperties(){
      return getProperties(true);
   }
   
   public List<Property> getProperties(boolean includeSuper){
      if(Type.class.isInstance(value)) {
         List<Property> total = new ArrayList<Property>();
         SuperExtractor extractor = new SuperExtractor();
         Type type = (Type)value;
         
         while(type != null) {
            List<Property> functions = type.getProperties();
            total.addAll(functions);
            
            if(!includeSuper) {
               break;
            }
            type = extractor.extractor(type);
         }
         return total;
      }
      return Collections.emptyList();
   }
   
   public String getModule() {
      if(Module.class.isInstance(value)) {
         return ((Module)value).getName();
      }
      if(Type.class.isInstance(value)) {
         return ((Type)value).getModule().getName();
      }
      return null;
   }
   
   public String getResource() {
      if(path != null) {
         return path;
      }
      if(Module.class.isInstance(value)) {
         return ((Module)value).getPath().getPath();
      }
      if(Type.class.isInstance(value)) {
         return ((Type)value).getModule().getPath().getPath();
      }
      return null;
   }
   
   public boolean isModule(){
      return Module.class.isInstance(value);
   }
   
   public boolean isType() {
      return Type.class.isInstance(value);
   }
   
   @Override
   public String toString() {
      return name;
   }
}