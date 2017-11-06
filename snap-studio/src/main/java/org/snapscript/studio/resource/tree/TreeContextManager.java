package org.snapscript.studio.resource.tree;

import java.io.File;
import java.io.IOException;

import org.snapscript.common.Cache;
import org.snapscript.common.LeastRecentlyUsedCache;
import org.snapscript.studio.Workspace;

public class TreeContextManager {

   private final Cache<String, TreeContext> contexts;
   private final Workspace workspace;
   
   public TreeContextManager(Workspace workspace) {
      this(workspace, 1000);
   }
   
   public TreeContextManager(Workspace workspace, int capacity) {
      this.contexts = new LeastRecentlyUsedCache<String, TreeContext>();
      this.workspace = workspace;
   }
   
   public TreeContext getContext(File path, String project, String cookie, boolean isProject) throws IOException {
      String realPath = path.getCanonicalPath();
      File realFile = path.getCanonicalFile();
      String key = String.format("%s-%s-%s", realPath, project, cookie);
      TreeContext context = contexts.fetch(key);
            
      if(context == null) {
         context = new TreeContext(workspace, realFile, project, isProject);
         contexts.cache(key, context);
      }
      return context;
   }
}