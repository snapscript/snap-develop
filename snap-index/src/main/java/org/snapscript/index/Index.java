package org.snapscript.index;

import org.snapscript.core.Path;

public interface Index {
   IndexType getType();
   Object getOperation();
   String getName();
   Path getPath();
   int getLine();
   
}
