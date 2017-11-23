package org.snapscript.studio.index.classpath;

import java.util.Collections;
import java.util.Set;

import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

import com.google.common.reflect.ClassPath.ClassInfo;

public class SuperIndexNode extends ClassIndexNode {
   
   public SuperIndexNode(ClassInfo info) {
      super(info);
   }

   @Override
   public IndexType getType() {
      return IndexType.SUPER;
   }

   @Override
   public Set<IndexNode> getNodes() {
      return Collections.emptySet();
   }
   
   @Override
   public String toString(){
      return getFullName();
   }
   
}
