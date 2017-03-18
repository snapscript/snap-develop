
package org.snapscript.develop.complete;

import java.util.Set;

public class TokenFilter {

   private final UserExpression  complete;
   private final String prefix;
   
   public TokenFilter(UserExpression complete, String prefix) {
      this.complete = complete;
      this.prefix = prefix;
   }
   
   public boolean acceptToken(String text, String type) {
      if(text.startsWith(prefix)) {
         Set<String> types = complete.getTypes();
         return types.contains(type);
      }
      return false;
   }
}
