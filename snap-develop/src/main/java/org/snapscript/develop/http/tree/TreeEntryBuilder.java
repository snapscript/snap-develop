/*
 * TreeEntryBuilder.java December 2016
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

public class TreeEntryBuilder {

   public static void buildFolder(StringBuilder builder, TreeNode node) {
      builder.append(node.getIndent());
      builder.append("<li id=\"");
      builder.append(node.getPrefix());
      builder.append(node.getId());
      builder.append("\" title=\"");
      builder.append(node.getPath());
      
      if(node.isRoot()) {
         builder.append("\" data-icon=\"/img/toolbar/index_co.gif\" class=\"expanded folder\">");
      } else if(node.isExpand()) {
         builder.append("\" data-icon=\"/img/toolbar/fldr_obj.gif\" class=\"expanded folder\">");
      } else {
         builder.append("\" data-icon=\"/img/toolbar/fldr_obj.gif\" class=\"folder\">");
      }
      builder.append(node.getName());
      builder.append("\n");
   }

   public static void buildFile(StringBuilder builder, TreeNode node) {
      String icon = "data-icon=\"/img/toolbar/cu_obj.gif\"";
      String name = node.getName();
      
      if(name.endsWith(".gif")) {
         icon = "data-icon=\"/img/toolbar/image_obj.gif\"";
      } else if(name.endsWith(".png")) {
         icon = "data-icon=\"/img/toolbar/image_obj.gif\"";
      } else if(name.endsWith(".jpg")) {
         icon = "data-icon=\"/img/toolbar/image_obj.gif\"";    
      } else if(name.endsWith(".jar")) {
         icon = "data-icon=\"/img/toolbar/jar_src_obj.gif\"";                    
      }else if(!name.endsWith(".snap")){
         icon = "data-icon=\"/img/toolbar/file_obj.gif\"";
      }
      builder.append(node.getIndent());
      builder.append("<li ");
      builder.append(icon);
      builder.append(" id=\"");
      builder.append(node.getPrefix());
      builder.append(node.getId());
      builder.append("\" title=\"");
      builder.append(node.getPath());
      builder.append("\">");
      builder.append(node.getName());
      builder.append("\n");
   }
}
