package org.snapscript.studio.index.classpath;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

public class ClassIndexNode implements IndexNode {
   
   private Set<IndexNode> children;
   private String fullName;
   private String typeName;
   private ClassFile file;
   private String absolute;
   private String resource;
   private String module;
   private String name;
   private Class type;
   private int modifiers;
   
   public ClassIndexNode(ClassFile info) {
      this.modifiers = -1;
      this.file = info;
   }
   
   @Override
   public int getLine() {
      return -1;
   }

   @Override
   public String getResource(){
      if(resource == null) {
         resource = file.getLocation();
      }
      return resource;
   }
   
   @Override
   public String getAbsolutePath(){
      if(absolute == null) {
         absolute = file.getAbsolutePath();
      }
      return absolute;
   }
   
   @Override
   public String getModule() {
      if(module == null) {
         module = file.getModule();
      }
      return module;
   }
   
    
   @Override
   public String getName() {
      if(name == null) {
         name = file.getName();
      }
      return name;
   }

   @Override
   public String getTypeName() {
      if(typeName == null) {
         typeName = file.getTypeName();
      }
      return typeName;
   }

   @Override
   public String getFullName() {
      if(fullName == null) {
         fullName = file.getFullName();
      }
      return fullName;
   }

   @Override
   public IndexNode getConstraint() {
      return null;
   }

   @Override
   public IndexNode getParent() {
      Class type = getNodeClass();
      
      if(type != null) {
         Class parent = type.getDeclaringClass();
         
         if(parent != null) {
            return ClassIndexProcessor.getIndexNode(parent);
         }
      }
      return null;
   }
   
   @Override
   public boolean isNative(){
      return true;
   }
   
   @Override
   public boolean isPublic() {
      Class type = getNodeClass();
      
      if(type != null) {
         if(modifiers == -1) {
            modifiers = type.getModifiers();
         }
         return Modifier.isPublic(modifiers);
      }
      return false;
   }

   @Override
   public IndexType getType() {
      Class type = getNodeClass();
      
      if(type != null) {
         if(type.isInterface()) {
            return IndexType.TRAIT;
         }
         if(type.isEnum()) {
            return IndexType.ENUM;
         }
      }
      return IndexType.CLASS;
   }

   @Override
   public Set<IndexNode> getNodes() {
      if(children == null) {
         children = ClassIndexProcessor.getChildren(file);
      }
      return children;
   }
   
   private Class getNodeClass() {
      if(type == null) {
         try {
            type = file.loadClass();
         } catch(Throwable e) {
            return null;
         }
      }
      return type;
   }
   
   @Override
   public String toString(){
      return getFullName();
   }
   
}
