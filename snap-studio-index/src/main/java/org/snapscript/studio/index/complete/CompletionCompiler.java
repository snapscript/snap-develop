package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexFile;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;
import org.snapscript.studio.index.Indexer;

public class CompletionCompiler {

   private final Class<? extends CompletionFinder>[] finders;
   private final IndexDatabase database;
   
   public CompletionCompiler(IndexDatabase database, Class<? extends CompletionFinder>... finders) {
      this.database = database;
      this.finders = finders;
   }
   
   public Map<String, String> compile(CompletionRequest request) throws Exception {
      String source = convertSource(request);
      String resource = request.getResource();
      String complete = request.getComplete();
      int line = request.getLine();
      IndexFile file = database.getFile(resource, source);
      IndexNode node = file.getNodeAtLine(line);
      
      for(Class<? extends CompletionFinder> finderType : finders) {
         CompletionFinder finder = finderType.newInstance();
         UserText text = finder.parseExpression(complete);
         
         if(text != null) {
            Set<IndexNode> matches = finder.findMatches(database, node, text);
            Map<String, String> tokens = new TreeMap<String, String>();
            
            for(IndexNode match : matches) {
               String name = match.getName();
               IndexType type = match.getType();
               String typeName = type.getName();
               
               tokens.put(name, typeName);
            }
            return tokens;
         }
      }
      return Collections.emptyMap();
   }
   
   public static String convertSource(CompletionRequest request) {
      int line = request.getLine();
      String source = request.getSource();
      String lines[] = source.split("\\r?\\n");
      
      if(lines.length > line && line > 0) {
         StringBuilder builder = new StringBuilder();
         
         lines[line - 1] = ""; // remove the completion line
         
         for(String entry : lines) {
            builder.append(entry);
            builder.append("\n");
         }
         return builder.toString();
      }
      return source;
   }
}
