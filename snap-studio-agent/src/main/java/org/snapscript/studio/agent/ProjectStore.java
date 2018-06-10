package org.snapscript.studio.agent;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.snapscript.common.store.RemoteStore;
import org.snapscript.common.store.Store;

public class ProjectStore implements Store {

   private String project;
   private Store store;
   
   public ProjectStore(URI root) {
      this.store = new RemoteStore(root);
   }
   
   public void update(String project) {
      this.project = project;
   }

   @Override
   public InputStream getInputStream(String resource) {
      String path = getPath(project, resource);
      return store.getInputStream(path);
   }
   
   @Override
   public OutputStream getOutputStream(String resource) {
      String path = getPath(project, resource);
      return store.getOutputStream(path);
   }
   
   public static String getPath(String project, String resource) {
      if(project != null) {
         if(!project.startsWith("/")) {
            project = "/" + project;
         }
         if(!project.endsWith("/")) {
            project = project + "/";
         }
         if(resource.startsWith("/")) {
            resource = resource.substring(1);
         }
         return project.concat(resource).replace("//", "/");
      }
      return resource.replace("//", "/");
   }
}