
package org.snapscript.develop.complete;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.snapscript.develop.common.TypeNode;

public class Completion {

   private final Map<String, TypeNode> types;
   private final Map<String, String> tokens;
   private final String resource;
   private final String complete;
   private final String source;
   private final String prefix;
   private final File root;
   private final int line;
   
   public Completion(File root, String source, String resource, String prefix, String complete, int line) {
      this.types = new HashMap<String, TypeNode>();
      this.tokens = new TreeMap<String, String>();
      this.resource = resource;
      this.complete = complete;
      this.source = source;
      this.prefix = prefix;
      this.line = line;
      this.root = root;
   }
   
   public Map<String, TypeNode> getTypes() {
      return types;
   }
   
   public Map<String, String> getTokens() {
      return tokens;
   }
   
   public File getRoot() {
      return root;
   }

   public String getResource() {
      return resource;
   }

   public String getComplete() {
      return complete;
   }

   public String getSource() {
      return source;
   }

   public String getPrefix() {
      return prefix;
   }

   public int getLine() {
      return line;
   } 
}
