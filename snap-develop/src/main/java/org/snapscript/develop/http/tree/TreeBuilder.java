/*
 * TreeBuilder.java December 2016
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

public class TreeBuilder {

   public static String createTree(TreeContext context, String treeId, boolean foldersOnly, int folderDepth) throws Throwable {
      StringBuilder builder = new StringBuilder();
      builder.append("<div id=\""+treeId+"\">\n");
      builder.append("<ul id=\"treeData\" style=\"display: none;\">\n");
      TreeDirectory tree = new TreeDirectory(
               context,
               foldersOnly,
               folderDepth);
      tree.buildTree(builder);
      builder.append("</ul>\n");
      builder.append("</div>\n");
      return builder.toString();
   }
   


}
