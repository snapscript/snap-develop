
package org.snapscript.develop.resource.project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;

public class ProjectFileCache {

   private final Map<String, ProjectFile> cache;
   private final ProjectBuilder builder;
   
   public ProjectFileCache(ProjectBuilder builder) {
      this.cache = new ConcurrentHashMap<String, ProjectFile>();
      this.builder = builder;
   }
   
   public ProjectFile getFile(Path path) throws Exception {
      String projectPath = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      ProjectFile file = cache.get(projectPath);
      
      if(file == null || file.isStale()) {
         Project project = builder.createProject(path);
         ProjectFileSystem fileSystem = project.getFileSystem();
         
         file = fileSystem.readFile(projectPath);
         cache.put(projectPath, file);
      }
      return file;
   }
   
}
