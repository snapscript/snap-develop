/*
 * TreeDirectory.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.http.tree;

import static org.snapscript.develop.http.tree.TreeConstants.INDENT;
import static org.snapscript.develop.http.tree.TreeConstants.PREFIX;
import static org.snapscript.develop.http.tree.TreeConstants.ROOT;
import static org.snapscript.develop.http.tree.TreeEntryBuilder.buildFile;
import static org.snapscript.develop.http.tree.TreeEntryBuilder.buildFolder;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TreeDirectory {
   
   private final TreeContext context;
   private final boolean foldersOnly;
   private final int folderDepth;
   
   public TreeDirectory(TreeContext context) {
      this(context, false);
   }
   
   public TreeDirectory(TreeContext context, boolean foldersOnly) {
      this(context, foldersOnly, Integer.MAX_VALUE);
   }
   
   public TreeDirectory(TreeContext context, boolean foldersOnly, int folderDepth) {
      this.foldersOnly = foldersOnly;
      this.folderDepth = folderDepth;
      this.context = context;
   }
   
   public void buildTree(StringBuilder builder) throws Exception {
      File root = context.getRoot();
      String project = context.getProject();
      Set<String> folders = context.getExpandFolders();
      TreeNode node = new TreeNode.Builder(root)
         .withPath(ROOT + project)
         .withIndent(INDENT)
         .withPrefix(PREFIX)
         .withId("/" + project)
         .withDepth(folderDepth)
         .withExpand(!folders.isEmpty())
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
                  String prefix = node.getPrefix() + node.getId() + "/";
                  
                  builder.append(node.getIndent());
                  builder.append("<ul>\n");
                  
                  for(File entry : list) {
                     String title = entry.getName();
                     String nextPath = node.getPath() + "/" + title;
                     TreeNode next = new TreeNode.Builder(entry)
                           .withPath(nextPath)
                           .withIndent(node.getIndent() + INDENT)
                           .withPrefix(prefix)
                           .withId(title)
                           .withDepth(folderDepth -1)
                           .withExpand(context.expand(nextPath))
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