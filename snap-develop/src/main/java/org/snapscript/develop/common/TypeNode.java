
package org.snapscript.develop.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.snapscript.core.Module;
import org.snapscript.core.Type;
import org.snapscript.core.define.SuperExtractor;
import org.snapscript.core.function.Function;
import org.snapscript.core.property.Property;

public class TypeNode {

   private final Object value;
   private final String name;
   
   public TypeNode(Type value, String name) {
      this.value = value;
      this.name = name;
   }
   
   public TypeNode(Module value, String name) {
      this.value = value;
      this.name = name;
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
