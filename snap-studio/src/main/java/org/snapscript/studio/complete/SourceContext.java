package org.snapscript.studio.complete;

import java.util.Map;

import org.snapscript.studio.search.TypeNode;

public class SourceContext {

   private final Map<String, String> tokens;
   private final TypeNode type;
   
   public SourceContext(TypeNode type, Map<String, String> tokens) {
      this.tokens = tokens;
      this.type = type;
   }
   
   public Map<String, String> getTokens(){
      return tokens;
   }
   
   public TypeNode getType(){
      return type;
   }
}