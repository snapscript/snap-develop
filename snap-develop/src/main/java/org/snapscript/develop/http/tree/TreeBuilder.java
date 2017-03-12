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

import org.snapscript.develop.http.display.DisplayModelResolver;
import org.snapscript.develop.http.resource.template.TemplateModel;

public class TreeBuilder {

   private final DisplayModelResolver resolver;
   
   public TreeBuilder(DisplayModelResolver resolver) {
      this.resolver = resolver;
   }
   
   public String createTree(TreeContext context, String treeId, boolean foldersOnly, int folderDepth) throws Throwable {
      StringBuilder builder = new StringBuilder();
      builder.append("<div id=\""+treeId+"\">\n");
      builder.append("<ul id=\"treeData\" style=\"display: none;\">\n");
      TemplateModel model = resolver.getModel();
      TreeDirectory tree = new TreeDirectory(
               context,
               model,
               foldersOnly,
               folderDepth);
      tree.buildTree(builder);
      builder.append("</ul>\n");
      builder.append("</div>\n");
      return builder.toString();
   }
   


}
