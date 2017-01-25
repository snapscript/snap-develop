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
