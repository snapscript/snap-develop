package org.snapscript.develop.http.tree;

import static org.snapscript.develop.http.tree.TreeConstants.ID;
import static org.snapscript.develop.http.tree.TreeConstants.INDENT;
import static org.snapscript.develop.http.tree.TreeConstants.ROOT;
import static org.snapscript.develop.http.tree.TreeEntryBuilder.buildFile;
import static org.snapscript.develop.http.tree.TreeEntryBuilder.buildFolder;

import java.io.File;
import java.util.List;

public class TreeDirectory {
   
   private final TreeFolderExpander expander;
   private final String project;
   private final String expand;
   private final File root;
   private final boolean foldersOnly;
   private final int folderDepth;
   
   public TreeDirectory(File root, String project) {
      this(root, project, null);
   }
   
   public TreeDirectory(File root, String project, String expand) {
      this(root, project, expand, false);
   }
   
   public TreeDirectory(File root, String project, String expand, boolean foldersOnly) {
      this(root, project, expand, foldersOnly, Integer.MAX_VALUE);
   }
   
   public TreeDirectory(File root, String project, String expand, boolean foldersOnly, int folderDepth) {
      this.expander = new TreeFolderExpander(project, expand);
      this.foldersOnly = foldersOnly;
      this.folderDepth = folderDepth;
      this.project = project;
      this.expand = expand;
      this.root = root;
   }
   
   public void buildTree(StringBuilder builder) throws Exception {
      TreeNode node = new TreeNode.Builder(root)
         .withPath(ROOT + project)
         .withIndent(INDENT)
         .withPrefix(ID)
         .withId(1)
         .withDepth(folderDepth)
         .withExpand(expand != null)
         .withRoot(true)
         .build();
      
      buildTree(builder, node);
   }
   
   private void buildTree(StringBuilder builder, TreeNode node) throws Exception {
      String name = node.getName();
      int folderDepth = node.getDepth();
      
      if(folderDepth > 0) {
         if(node.isDirectory()) {
            if(!name.startsWith(".")) { // ignore directories starting with "."
               buildFolder(builder, node);
               
               List<File> list = node.getFiles();
               
               if(!list.isEmpty()) {
                  String prefix = node.getPrefix() + node.getId() + ".";
                  
                  builder.append(node.getIndent());
                  builder.append("<ul>\n");
                  
                  for(File entry : list) {
                     String title = entry.getName();
                     String nextPath = node.getPath() + "/" + title;
                     TreeNode next = new TreeNode.Builder(entry)
                           .withPath(nextPath)
                           .withIndent(node.getIndent() + INDENT)
                           .withPrefix(prefix)
                           .withDepth(folderDepth -1)
                           .withExpand(expander.expand(nextPath))
                           .build();
                     
                     buildTree(builder, next);
                  }
                  builder.append(node.getIndent());
                  builder.append("</ul>\n");
               }
            }
         } else {
            if(!foldersOnly) {
               buildFile(builder, node);
            }
         }
      }
   }   
}