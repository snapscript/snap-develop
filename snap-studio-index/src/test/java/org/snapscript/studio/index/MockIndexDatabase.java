package org.snapscript.studio.index;

import java.util.Map;

public class MockIndexDatabase implements IndexDatabase {
   
   private Indexer indexer;
   
   public void setIndexer(Indexer indexer) {
      this.indexer = indexer;
   }

   @Override
   public Map<String, IndexFile> getFiles() throws Exception {
      return null;
   }

   @Override
   public IndexNode getTypeNode(String typeName) throws Exception {
      return null;
   }

   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression) throws Exception {
      return null;
   }
   
   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String expression, boolean ignoreCase) throws Exception {
      return null;
   }

   @Override
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      return null;
   }

   @Override
   public IndexFile getFile(String resource, String source) throws Exception {
      return indexer.index(resource, source);
   }

   @Override
   public IndexNode getDefaultImport(String module, String name) throws Exception {
      return null;
   }
   
}