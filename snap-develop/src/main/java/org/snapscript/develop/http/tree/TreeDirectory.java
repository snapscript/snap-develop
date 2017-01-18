package org.snapscript.develop.http.tree;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class TreeDirectory {
   
   private final Comparator<File> comparator;
   private final File root;
   private final String project;
   private final String expand;
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
      this.comparator = new TreeFileComparator();
      this.root = root;
      this.project = project;
      this.expand = expand;
      this.foldersOnly = foldersOnly;
      this.folderDepth = folderDepth;
   }
   
   public void buildTree(StringBuilder builder) throws Exception {
      String expandPath = null;

      if(expand != null) {
         expandPath = expand;

         if(expandPath.startsWith("/")) {
            expandPath = expandPath.substring(1); 
         } 
         if(expandPath.endsWith("/")) {
            int length = expandPath.length();
            expandPath = expand.substring(0, length - 1);
         }
         expandPath = "/resource/" + project + "/" + expandPath;
      }
      if(expandPath != null) {
         buildTree(builder, root, expandPath, "/resource/" + project, "  ", "id", 1, foldersOnly, folderDepth, true);
      } else {
         buildTree(builder, root, expandPath, "/resource/" + project, "  ", "id", 1, foldersOnly, folderDepth, false);
      }
   }

   private void buildTree(StringBuilder builder, File currentFile, String expandPath, String currentPath, String pathIndent, String idPrefix, int id, boolean foldersOnly, int folderDepth, boolean openPath) throws Exception {
      String name = currentFile.getName();
      
      if(folderDepth > 0) {
         if(currentFile.isDirectory()) {
            if(!name.startsWith(".")) { // ignore directories starting with "."
               buildFolder(builder, currentPath, pathIndent, idPrefix, id, openPath, name);
               
               File[] list = currentFile.listFiles();
               Arrays.sort(list, comparator);
               
               if(list != null && list.length > 0) {
                  idPrefix = idPrefix + id + ".";
                  builder.append(pathIndent);
                  builder.append("<ul>\n");
                  for(int i = 0; i < list.length; i++) {
                     File entry = list[i];
                     String title = entry.getName();
                     String nextPath = currentPath + "/" + title;
                     
                     if(expandPath == null || !expandPath.startsWith(nextPath)) {
                        buildTree(builder, entry, expandPath, nextPath, pathIndent + "  ", idPrefix, i + 1, foldersOnly, folderDepth -1,false);
                     } else {
                        buildTree(builder, entry, expandPath, nextPath, pathIndent + "  ", idPrefix, i + 1, foldersOnly, folderDepth -1, true);
                     }
                  }
                  builder.append(pathIndent);
                  builder.append("</ul>\n");
               }
            }
         } else {
            if(!foldersOnly) {
               buildFile(builder, currentPath, pathIndent, idPrefix, id, name);
            }
         }
      }
   }

   private void buildFolder(StringBuilder builder, String currentPath, String pathIndent, String idPrefix, int id, boolean openPath, String name) {
      builder.append(pathIndent);
      builder.append("<li id=\"");
      builder.append(idPrefix);
      builder.append(id);
      builder.append("\" title=\"");
      builder.append(currentPath);
      
      if(openPath) {
         builder.append("\" data-icon=\"/img/toolbar/fldr_obj.gif\" class=\"expanded folder\">");
      } else {
         builder.append("\" data-icon=\"/img/toolbar/fldr_obj.gif\" class=\"folder\">");
      }
      builder.append(name);
      builder.append("\n");
   }

   private void buildFile(StringBuilder builder, String currentPath, String pathIndent, String idPrefix, int id, String name) {
      String icon = "data-icon=\"/img/toolbar/cu_obj.gif\"";
      
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
      builder.append(pathIndent);
      builder.append("<li ");
      builder.append(icon);
      builder.append(" id=\"");
      builder.append(idPrefix);
      builder.append(id);
      builder.append("\" title=\"");
      builder.append(currentPath);
      builder.append("\">");
      builder.append(name);
      builder.append("\n");
   }
   
}