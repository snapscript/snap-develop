package org.snapscript.develop.http.tree;

public class TreeFolderExpander {

   private final String expand;
   
   public TreeFolderExpander(String project, String expand) {
      this.expand = TreePathFormatter.formatPath(project, expand);
   }
   
   public boolean expand(String path) {
      return expand != null && expand.startsWith(path);
   }
}
