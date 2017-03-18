
package org.snapscript.develop.common;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.configuration.ConfigurationClassLoader;

public class TypeNodeFinder {

   private final ResourceTypeLoader compiler;
   
   public TypeNodeFinder(ConfigurationClassLoader loader, ProcessLogger logger) {
      this.compiler = new ResourceTypeLoader(loader, logger);
   }
   
   public Map<String, TypeNode> parse(File root, String project, String resource, String source) {
      Map<String, TypeNode> types = new HashMap<String, TypeNode>();
      
      try {
         Map<String, TypeNode> nodes = compiler.compileSource(root, resource, source);
         Set<String> names = nodes.keySet();
         
         for(String name : names) {
            if(!name.contains(".")) {
               TypeNode node = nodes.get(name);
               String path = node.getResource();
               
               if(path.equals(resource)) {
                  types.put(name, node);
               }
            }
         }
      }catch(Exception cause) {
         return Collections.emptyMap();
      }
      return types;
   }
}
