
package org.snapscript.develop.resource.loader;

public class ClassFilter {

   private final String prefix;
   
   public ClassFilter(String prefix) {
      this.prefix = prefix;
   }
   
   public boolean accept(String name) {
      if(name != null) {
         if(name.startsWith(prefix)) {
            try {
               Class.forName(name);
            } catch(Exception e) {
               return false;
            }
            return true;
         }
      }
      return false;
   }
}
