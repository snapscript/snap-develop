package org.snapscript.develop.resource.tree;

import java.io.File;
import java.io.IOException;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;

public class TreeContextManager {

   private final Cache<String, TreeContext> contexts;
   
   public TreeContextManager() {
      this(1000);
   }
   
   public TreeContextManager(int capacity) {
      this.contexts = new LeastRecentlyUsedCache<String, TreeContext>();
   }
   
   public TreeContext getContext(File path, String project, String cookie) throws IOException {
      String realPath = path.getCanonicalPath();
      File realFile = path.getCanonicalFile();
      String key = String.format("%s-%s-%s", realPath, project, cookie);
      TreeContext context = contexts.fetch(key);
            
      if(context == null) {
         context = new TreeContext(realFile, project);
         contexts.cache(key, context);
      }
      return context;
   }
}
