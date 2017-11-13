package org.snapscript.studio.index;

import java.util.Set;

public interface IndexNode {
   int getLine();
   String getName();
   String getTypeName();
   String getFullName();
   IndexNode getConstraint();
   IndexNode getParent();
   IndexType getType();
   Set<IndexNode> getNodes();
}
