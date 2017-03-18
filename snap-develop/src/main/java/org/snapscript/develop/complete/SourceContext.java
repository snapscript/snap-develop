
package org.snapscript.develop.complete;

import java.util.Map;

import org.snapscript.develop.common.TypeNode;

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
