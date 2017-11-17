package org.snapscript.studio.index.classpath;

import java.util.Map;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexFile;
import org.snapscript.studio.index.IndexNode;

public class ClassPathIndexDatabase implements IndexDatabase {
   
   private final ClassPathIndexScanner scanner;
   private final IndexDatabase database;
   
   public ClassPathIndexDatabase(IndexDatabase database, ClassPathIndexScanner scanner) {
      this.database = database;
      this.scanner = scanner;
   }

   @Override
   public IndexNode getTypeNode(String type) throws Exception {
      IndexNode node = database.getTypeNode(type);
      
      if(node == null) {
         return scanner.getTypeNodes().get(type);
      }
      return node;
   }

   @Override
   public Map<String, IndexNode> getTypeNodesMatching(String regex) throws Exception {
      Map<String, IndexNode> matches = database.getTypeNodesMatching(regex);
      Map<String, IndexNode> extra = scanner.getTypeNodesMatching(regex);
      
      if(!matches.isEmpty()) {
         extra.putAll(matches);
      }
      return extra;
   }

   @Override
   public Map<String, IndexNode> getTypeNodes() throws Exception {
      Map<String, IndexNode> matches = database.getTypeNodes();
      Map<String, IndexNode> extra = scanner.getTypeNodes();
      
      if(!matches.isEmpty()) {
         extra.putAll(matches);
      }
      return extra;
   }

   @Override
   public Map<String, IndexFile> getFiles() throws Exception {
      return database.getFiles();
   }

   @Override
   public IndexFile getFile(String resource, String source) throws Exception {
      return database.getFile(resource, source);
   }

}
