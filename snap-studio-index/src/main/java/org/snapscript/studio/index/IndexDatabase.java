package org.snapscript.studio.index;

import java.util.Map;

public interface IndexDatabase {
   IndexNode getDefaultImport(String module, String name) throws Exception;
   IndexNode getTypeNode(String type) throws Exception;
   Map<String, IndexNode> getTypeNodesMatching(String regex) throws Exception;
   Map<String, IndexNode> getTypeNodesMatching(String regex, boolean ignoreCase) throws Exception;
   Map<String, IndexNode> getTypeNodes() throws Exception;
   Map<String, IndexFile> getFiles() throws Exception;
   Map<String, IndexNode> getNodesInScope(IndexNode node) throws Exception;
   IndexFile getFile(String resource, String source) throws Exception;
}
