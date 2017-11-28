package org.snapscript.studio.index.complete;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexDumper;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;
import org.snapscript.studio.index.SourceFile;

public class CompletionCompiler {

   private final Class<? extends CompletionFinder>[] finders;
   private final IndexDatabase database;
   
   public CompletionCompiler(IndexDatabase database, Class<? extends CompletionFinder>... finders) {
      this.database = database;
      this.finders = finders;
   }
   
   public CompletionResponse compile(CompletionRequest request) throws Exception {
      int line = request.getLine();
      EditContext input = EditContextExtractor.extractContext(request);
      String source = input.getSource();
      String complete = input.getExpression();
      String resource = request.getResource();
      SourceFile file = database.getFile(resource, source);
      IndexNode node = file.getNodeAtLine(line);
      IndexNode root = file.getRootNode();
      String details = IndexDumper.dump(root);
      System.out.println(complete);
      System.out.println(source);
      for(Class<? extends CompletionFinder> finderType : finders) {
         CompletionFinder finder = finderType.newInstance();
         InputText text = finder.parseExpression(complete);
         
         if(text != null) {
            Set<IndexNode> matches = finder.findMatches(database, node, text);
            Map<String, IndexNode> nodes = database.getTypeNodes();
            Map<String, String> tokens = new TreeMap<String, String>();
            
            for(IndexNode match : matches) {
               String name = match.getName();
               IndexType type = match.getType();
               String fullName = match.getFullName();
               
               if(type == IndexType.MEMBER_FUNCTION) {
                  type = IndexType.FUNCTION;
               }
               if(type == IndexType.IMPORT) {
                  IndexNode imported = nodes.get(fullName);
                  
                  if(imported != null) {
                     name = imported.getName();
                     type = imported.getType();
                  } else {
                     type = IndexType.CLASS; // hack job
                  }
               }
               String category = type.getName();
               tokens.put(name, category);
            }
            return new CompletionResponse(tokens, complete, details);
         }
      }
      return new CompletionResponse();
   }
}
