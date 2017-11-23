package org.snapscript.studio.index;

import java.util.Set;

public interface IndexNode {
   int getLine();
   boolean isPublic();
   boolean isNative();
   String getResource();
   String getAbsolutePath();
   String getName();
   String getTypeName();
   String getFullName();
   String getModule();
   IndexNode getConstraint();
   IndexNode getParent();
   IndexType getType();
   Set<IndexNode> getNodes();
   // this node should be implemented to form a cache for super lookups
   //Set<IndexNode> getSuperNodes(); 
}
 