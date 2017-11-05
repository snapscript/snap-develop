package org.snapscript.studio.resource.project;

import java.io.File;

public class ProjectLayout {

   private final String[] paths;
   
   public ProjectLayout(String... paths) {
      this.paths = paths;
   }
   
   public String getRealPath(File projectPath, String resource) { //  "/demo/mario/MarioGame.snap" -> "/demo/mario/src/mario/MarioGame.snap"
      File resourcePath = new File(projectPath, resource);
      
      if(!resourcePath.exists()) {
         for(String path : paths) {
            File file = new File(projectPath, path + "/" + resource);
            if(file.exists()) {
               String relativePath = getRelativeFile(projectPath, file);
               if(!relativePath.startsWith("/")) {
                  return "/" +relativePath;
               }
               return relativePath;
            }
         }
      }
      return resource;
   }
   
   public String getDownloadPath(File projectPath, String resource) { // "/demo/mario/src/mario/MarioGame.snap" -> "/demo/mario/MarioGame.snap"
      for(String path : paths) {
         path = path.replace("\\", "/");
         
         if(!path.startsWith("/")) {
            path = "/" + path;
         }
         if(resource.startsWith(path)) {
            int length = path.length();
            return resource.substring(length);
         }
      }
      return resource;
   }
   
   private String getRelativeFile(File root, File file) {
      return root.toURI().relativize(file.toURI()).getPath();
   }
}
