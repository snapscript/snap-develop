package org.snapscript.studio.index.classpath;

import java.net.URL;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassIndexNode implements IndexNode {
   
   private ClassInfo info;
   private Class type;
   private String absolute;
   private String resource;
   private String module;
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
      return ClassIndexProcessor.getModule(info);
   }
   
    
   @Override
   public String getName() {
      return ClassIndexProcessor.getName(info);
   }

   @Override
   public String getTypeName() {
      return ClassIndexProcessor.getTypeName(info);
   }

   @Override
   public String getFullName() {
      return ClassIndexProcessor.getFullName(info);
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
      return ClassIndexProcessor.getChildren(info);
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
   
}
