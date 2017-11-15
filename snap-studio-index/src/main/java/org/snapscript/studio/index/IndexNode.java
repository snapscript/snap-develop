package org.snapscript.studio.index;

import java.util.Set;

public interface IndexNode {
   int getLine();
   String getResource();
   String getName();
   String getTypeName();
   String getFullName();
   IndexNode getConstraint();
   IndexNode getParent();
   IndexType getType();
   Set<IndexNode> getNodes();
}
 