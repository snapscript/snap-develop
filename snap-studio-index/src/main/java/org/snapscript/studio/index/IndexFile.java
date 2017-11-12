package org.snapscript.studio.index;

import java.util.Map;

public interface IndexFile {
   IndexNode getRootNode();
   IndexNode getNodeAtLine(int line);
   Map<String, IndexNode> getNodesInScope(int line);
   Map<String, IndexNode> getTypeNodes();
}
