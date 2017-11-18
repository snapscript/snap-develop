package org.snapscript.studio.index.complete;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.studio.index.IndexDatabase;
import org.snapscript.studio.index.IndexNode;
import org.snapscript.studio.index.IndexSearcher;
import org.snapscript.studio.index.IndexType;
import org.snapscript.studio.index.classpath.BootstrapClassPath;

public class FindConstructorsInScope implements CompletionFinder {

   private static final Pattern PATTERN = Pattern.compile(".*new\\s+([a-zA-Z0-9_]*)$");
   
   @Override
   public UserText parseExpression(String expression) {
      Matcher matcher = PATTERN.matcher(expression);
      
      if(matcher.matches()) {
         String unfinished = matcher.group(1);
         
         return new UserText(null, unfinished);
      }
      return null;
   }

   @Override
   public Set<IndexNode> findMatches(IndexDatabase database, IndexNode node, UserText text) throws Exception {
      Map<String, IndexNode> expandedScope = getTypesAvailable(database, node);
      Set<Entry<String, IndexNode>> entries = expandedScope.entrySet();
      Map<String, IndexNode> allNodes = database.getTypeNodes();
      String unfinished = text.getUnfinished();
      
      if(!entries.isEmpty()) {
         Set<IndexNode> matched = new HashSet<IndexNode>();
         
         for(Entry<String, IndexNode> entry : entries) {
            String name = entry.getKey();
            IndexNode value = entry.getValue();
            IndexType type = value.getType();
            
            if(name.startsWith(unfinished)) {
               if(type.isImport() || type.isClass()) {
                  String fullName = value.getFullName();
                  IndexNode imported = allNodes.get(fullName);
                  
                  if(imported != null) {
                     Set<IndexNode> nodes = imported.getNodes();
                     
                     for(IndexNode child : nodes) {
                        IndexType childType = child.getType();
                        
                        if(childType.isConstructor()) {
                           matched.add(child);
                        }
                     }
                  }
               } else if(type.isConstructor()) {
                  matched.add(value);
               }
            }
         }
         return matched;
      }
      return Collections.emptySet();
   }
   
   private Map<String, IndexNode> getTypesAvailable(IndexDatabase database, IndexNode node) {
      Map<String, IndexNode> expandedScope = IndexSearcher.getNodesInScope(node);
      Map<String, IndexNode> available = new HashMap<String, IndexNode>();
      
      available.putAll(expandedScope);
      available.putAll(BootstrapClassPath.getDefaultImportClasses());
      
      return Collections.unmodifiableMap(available);
   }
}
