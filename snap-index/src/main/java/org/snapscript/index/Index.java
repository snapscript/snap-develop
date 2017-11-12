package org.snapscript.index;

import org.snapscript.core.Path;
import org.snapscript.tree.constraint.Constraint;

public interface Index {
   IndexType getType();
   Object getOperation();
   Constraint getConstraint();
   String getName();
   Path getPath();
   int getLine();
   
}
