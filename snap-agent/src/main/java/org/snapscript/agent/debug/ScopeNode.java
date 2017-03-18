
package org.snapscript.agent.debug;

import java.util.List;

public interface ScopeNode {
   int getDepth();
   String getName();
   String getPath();
   List<ScopeNode> getNodes();
}
