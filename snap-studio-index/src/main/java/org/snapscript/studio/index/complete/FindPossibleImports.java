package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;

public class FindPossibleImports implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile("\\s*import\\s+([a-zA-Z0-9_]*)$");
   private static final String[] IGNORE_PREFIXES = {
      "java.",
      "javax.",
   };
   
   @Override
   public UserExpression parseExpression(String expression) {
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String unfinished = matcher.group(1);
         return new UserExpression(null, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserExpression text) {
      try {
         Map<String, IndexNode> allTypes = database.getTypeNodes();
         Set<Entry<String, IndexNode>> entries = allTypes.entrySet();
         String unfinished = text.getUnfinished();
         
         if(!entries.isEmpty()) {
            Set<IndexNode> matched = new HashSet<IndexNode>();
            
            for(Entry<String, IndexNode> entry : entries) {
               String name = entry.getKey();
               IndexNode value = entry.getValue();
               
               if(name.contains(unfinished) && value.isPublic()) {
                  String importName = getImportName(name);
                  ImportIndexNode imported = new ImportIndexNode(value, importName);
                  matched.add(imported);
               }
            }
            return matched;
         }
      } catch(Throwable e){
         e.printStackTrace();
      }
      return Collections.emptySet();
   }
   
   private static String getImportName(String fullName) {
      for(String prefix : IGNORE_PREFIXES) {
         if(fullName.startsWith(prefix)) {
            int length = prefix.length();
            return fullName.substring(length);
         }
      }
      return fullName;
   }
   
   private static class ImportIndexNode extends ProxyIndexNode {

      private final String name;
      
      public ImportIndexNode(IndexNode node, String name) {
         super(node);
         this.name = name;
      }
      
      @Override
      public String getFullName(){
         return name;
      }
      
      @Override
      public String getName() {
         return name;
      }
      
      @Override
      public String getTypeName(){
         return name;
      }
      
   }
}
