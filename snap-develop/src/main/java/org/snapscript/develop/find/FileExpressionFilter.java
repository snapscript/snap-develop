

package org.snapscript.develop.find;

import java.io.File;
import java.io.FilenameFilter;

public class FileExpressionFilter implements FilenameFilter {
   
   private final ExpressionResolver resolver;
   private final PathBuilder builder;
   
   public FileExpressionFilter(ExpressionResolver resolver, PathBuilder builder) {
      this.resolver = resolver;
      this.builder = builder;
   }

   @Override
   public boolean accept(File file, String name) {
      if(file.isFile()) {
         String source = builder.buildPath(file);
         String result = resolver.match(source);
         
         if(result != null) {
            return true;
         }
      }
      return false;
   }

}
