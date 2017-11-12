package org.snapscript.studio.index;

import org.snapscript.core.Path;

public interface Index {
   IndexType getType();
   Object getOperation();
   String getConstraint();
   String getName();
   String getModule();
   Path getPath();
   int getLine();
   
}
