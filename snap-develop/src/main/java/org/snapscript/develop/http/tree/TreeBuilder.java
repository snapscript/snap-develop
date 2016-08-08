package org.snapscript.develop.http.tree;

import java.io.File;

public class TreeBuilder {

   public static String createTree(File treePath, String treeId, String expand, boolean foldersOnly, int folderDepth) throws Throwable {
      String projectName = treePath.getName();
      StringBuilder builder = new StringBuilder();
      builder.append("<div id=\""+treeId+"\">\n");
      builder.append("<ul id=\"treeData\" style=\"display: none;\">\n");
      TreeDirectory tree = new TreeDirectory(
               treePath, 
               projectName, 
               expand,
               foldersOnly,
               folderDepth);
      tree.buildTree(builder);
      builder.append("</ul>\n");
      builder.append("</div>\n");
      return builder.toString();
   }
   


}
