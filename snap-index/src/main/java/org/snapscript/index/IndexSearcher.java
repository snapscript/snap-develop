package org.snapscript.index;

import java.util.Map;

public interface IndexSearcher {
   IndexNode getRootNode();
   IndexNode getNodeAtLine(int line);
   Map<String, IndexNode> getNodesInScope(int line);
   Map<String, IndexNode> getTypeNodes();
}
