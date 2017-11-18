package org.snapscript.studio.index.classpath;

import java.net.URL;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassIndexNode implements IndexNode {
   
   private Set<IndexNode> children;
   private String fullName;
   private String typeName;
   private ClassInfo info;
   private String absolute;
   private String resource;
   private String module;
   private String name;
   private Class type;
   private URL url;
   
   public ClassIndexNode(ClassInfo info) {
      this.info = info;
   }
   
   @Override
   public int getLine() {
      return -1;
   }

   @Override
   public String getResource(){
      if(resource == null) {
         resource = ClassIndexProcessor.getResource(info);
      }
      return resource;
   }
   
   @Override
   public String getAbsolutePath(){
      if(absolute == null) {
         absolute = ClassIndexProcessor.getAbsolutePath(info);
      }
      return absolute;
   }
   
   @Override
   public String getModule() {
      if(module == null) {
         module = ClassIndexProcessor.getModule(info);
      }
      return module;
   }
   
    
   @Override
   public String getName() {
      if(name == null) {
         name = ClassIndexProcessor.getName(info);
      }
      return name;
   }

   @Override
   public String getTypeName() {
      if(typeName == null) {
         typeName = ClassIndexProcessor.getTypeName(info);
      }
      return typeName;
   }

   @Override
   public String getFullName() {
      if(fullName == null) {
         fullName = ClassIndexProcessor.getFullName(info);
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
         children = ClassIndexProcessor.getChildren(info);
      }
      return children;
   }
   
   public URL getURL() {
      if(url == null) {
         url = info.url();
      }
      return url;
   }
   
   private Class getNodeClass() {
      if(type == null) {
         try {
            type = info.load();
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
