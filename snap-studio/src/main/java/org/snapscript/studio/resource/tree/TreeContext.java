package org.snapscript.studio.resource.tree;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class TreeContext implements TreeFolderExpander {
   
   private final Set<String> expand;
   private final String project;
   private final File root;
   
   public TreeContext(File root, String project) {
      this.expand = new CopyOnWriteArraySet<String>();
      this.project = project;
      this.root = root;
   }
   
   public File getRoot() {
      return root;
   }
   
   public String getProject() {
      return project;
   }
   
   public Set<String> getExpandFolders() {
      return Collections.unmodifiableSet(expand);
   }
   
   public TreeContext folderExpand(String path) {
      String result = TreePathFormatter.formatPath(project, path);
      expand.add(result);
      return this;
   }
   
   public TreeContext folderCollapse(String path) {
      String result = TreePathFormatter.formatPath(project, path);
      expand.remove(result);
      return this;
   }

   @Override
   public boolean expand(String path) {
      return path != null && expand.contains(path);
   }
   
}