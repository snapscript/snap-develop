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
         resource = ClassReflectionIndexer.getResource(info);
      }
      return resource;
   }
   
   @Override
   public String getAbsolutePath(){
      if(absolute == null) {
         absolute = ClassReflectionIndexer.getAbsolutePath(info);
      }
      return absolute;
   } 
    
   @Override
   public String getName() {
      return ClassReflectionIndexer.getName(info);
   }

   @Override
   public String getTypeName() {
      return ClassReflectionIndexer.getTypeName(info);
   }

   @Override
   public String getFullName() {
      return ClassReflectionIndexer.getFullName(info);
   }

   @Override
   public IndexNode getConstraint() {
      return null;
   }

   @Override
   public IndexNode getParent() {
      Class type = getNodeClass();
      Class parent = type.getDeclaringClass();
      
      if(parent != null) {
         return ClassReflectionIndexer.getIndexNode(parent);
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
      return ClassReflectionIndexer.getChildren(info);
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
