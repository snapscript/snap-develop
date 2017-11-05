package org.snapscript.studio.resource.project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;
import org.snapscript.studio.Workspace;

public class ProjectFileCache {

   private final Map<String, ProjectFile> cache;
   private final Workspace workspace;
   
   public ProjectFileCache(Workspace workspace) {
      this.cache = new ConcurrentHashMap<String, ProjectFile>();
      this.workspace = workspace;
   }
   
   public ProjectFile getFile(Path path) throws Exception {
      String pathKey = path.getPath();
      ProjectFile file = cache.get(pathKey);
      
      if(file == null || file.isStale()) {
         String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
         Project project = workspace.createProject(path);
         ProjectFileSystem fileSystem = project.getFileSystem();
         
         file = fileSystem.readFile(projectPath);
         cache.put(pathKey, file);
      }
      return file;
   }
   
}