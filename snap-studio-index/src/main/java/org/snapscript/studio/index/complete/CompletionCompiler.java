package org.snapscript.studio.index.complete;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexDumper;
import org.snapscript.studio.index.IndexFile;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexType;

public class CompletionCompiler {

   private final Class<? extends CompletionFinder>[] finders;
   private final IndexDatabase database;
   
   public CompletionCompiler(IndexDatabase database, Class<? extends CompletionFinder>... finders) {
      this.database = database;
      this.finders = finders;
   }
   
   public CompletionResponse compile(CompletionRequest request) throws Exception {
      String source = convertSource(request);
      String complete = extractUserText(request);
      String resource = request.getResource();
      int line = request.getLine();
      IndexFile file = database.getFile(resource, source);
      IndexNode node = file.getNodeAtLine(line);
      IndexNode root = file.getRootNode();
      String details = IndexDumper.dump(root);
      
      for(Class<? extends CompletionFinder> finderType : finders) {
         CompletionFinder finder = finderType.newInstance();
         UserText text = finder.parseExpression(complete);
         
         if(text != null) {
            Set<IndexNode> matches = finder.findMatches(database, node, text);
            Map<String, String> tokens = new TreeMap<String, String>();
            
            for(IndexNode match : matches) {
               String name = match.getName();
               IndexType type = match.getType();
               String fullName = match.getFullName();
               
               if(type == IndexType.MEMBER_FUNCTION) {
                  type = IndexType.FUNCTION;
               }
               if(type == IndexType.IMPORT) {
                  IndexNode imported = database.getTypeNode(fullName);
                  
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
            return new CompletionResponse(tokens, details);
         }
      }
      return new CompletionResponse();
   }
   
   private static String convertSource(CompletionRequest request) {
      int line = request.getLine();
      String source = request.getSource();
      String lines[] = source.split("\\r?\\n");
      
      if(lines.length >= line && line > 0) {
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
   
   private static String extractUserText(CompletionRequest request) {
      String completion = request.getComplete();
      int length = completion.length();
      int begin = length -1;
      
      while(begin > 0) {
         char next = completion.charAt(begin);
         
         if(isTerminal(next)) {
            return completion.substring(begin + 1, length);
         }
         begin--;
      }
      return completion;
   }
   
   private static boolean isTerminal(char value) {
      switch(value) {
      case ',': case '{':
      case '(': case '+':
      case '-': case '*':
      case '/': case '%':
      case '|': case '&':
      case '?': case ':':
      case '=': case '<':
      case '>':
         return true;
      }
      return false;
   }
}
