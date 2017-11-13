package org.snapscript.studio.index.classpath;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ClassPathIndexScanner {

   private final ClassLoader loader;
   
   public ClassPathIndexScanner(ClassLoader loader) {
      this.loader = loader;
   }
   
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      ImmutableSet<ClassInfo> types = ClassPath.from(loader).getAllClasses();
      
      if(!types.isEmpty()) {
         
      }
      return Collections.emptyMap();
   }
   
   private Set<IndexNode> getFunctionNodes(Class type) {
      return Collections.emptySet();
   }
   
   public Set<IndexNode> getFieldNodes(Class type) {
      return Collections.emptySet();
   }
   
   public Set<IndexNode> getInnerClassNodes(Class type) {
      return Collections.emptySet();
   }

   private static class ClassIndexNode implements IndexNode {
      
//      private final Set<IndexNode> nodes;
//      private final IndexNode parent;
//      private final String constraint;
//      private final String fullName;
//      private final String typeName;
//      private final String name;

      @Override
      public int getLine() {
         return -1;
      }

      @Override
      public String getName() {
         return null;
      }

      @Override
      public String getTypeName() {
         return null;
      }

      @Override
      public String getFullName() {
         return null;
      }

      @Override
      public IndexNode getConstraint() {
         return null;
      }

      @Override
      public IndexNode getParent() {
         return null;
      }

      @Override
      public IndexType getType() {
         return null;
      }

      @Override
      public Set<IndexNode> getNodes() {
         return null;
      }
      
      private Class getNodeClass() {
         return null;
      }
      
   }
}
