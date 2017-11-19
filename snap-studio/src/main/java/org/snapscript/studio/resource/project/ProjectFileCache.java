package org.snapscript.studio.resource.project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.snapscript.studio.core.Workspace;

public class ProjectFileCache {

   private final Map<String, ProjectFile> cache;
   private final Workspace workspace;
   
   public ProjectFileCache(Workspace workspace) {
      this.cache = new ConcurrentHashMap<String, ProjectFile>();
      this.workspace = workspace;
   }
   
   public ProjectFile getFile(String projectName, String projectPath) throws Exception {
      String pathKey = projectName + ":" + projectPath;
      ProjectFile file = cache.get(pathKey);
      
      if(file == null || file.isStale()) {
         Project project = workspace.createProject(projectName);
         ProjectFileSystem fileSystem = project.getFileSystem();
         
         file = fileSystem.readFile(projectPath);
         cache.put(pathKey, file);
      }
      return file;
   }
   
}