package org.snapscript.studio.resource.project;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.common.FilePatternScanner;
import org.snapscript.studio.common.resource.Resource;

public class ProjectDefaultResource implements Resource {
   
   private static final String[] DEFAULT_PATHS = new String[]{
      "README.md", 
      "**/README.md", 
      "README.txt", 
      "**/README.txt", 
      "*.snap", 
      "**/*.snap", 
      "*.*", 
      "**/*.*", 
      "*", 
      "**/*"};
   
   private final Workspace workspace;
   
   public ProjectDefaultResource(Workspace workspace){
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath(); 
      Project project = workspace.createProject(path);
      
      if(project == null) {
         throw new IllegalStateException("Could not find project: " + request);
      }
      File projectRoot = project.getProjectPath();
      String projectName = project.getProjectName();
      PrintStream stream = response.getPrintStream();
      String defaultFile = getDefaultFile(projectRoot, projectName);
      
      response.setStatus(Status.OK);
      response.setContentType("text/plain");
      
      workspace.getLogger().info("Default file: " + defaultFile);
      stream.print(defaultFile);
      stream.close();
   }
   
   private static String getDefaultFile(File projectRoot, String projectName) throws Exception {
      StringBuilder builder = new StringBuilder();
      
      for(String defaultPath : DEFAULT_PATHS) {
         List<File> files = FilePatternScanner.scan(defaultPath, projectRoot);
      
         if(!files.isEmpty()) {
            String file = files.get(0).getCanonicalPath();
            String root = projectRoot.getCanonicalPath();
            String resource = file.replace(root, "").replace(File.separatorChar,  '/');
            
            if(resource.startsWith("/")) {
               builder.append("/resource/");
               builder.append(projectName);
               builder.append(resource);
               return builder.toString();
            }
            builder.append("/resource/");
            builder.append(projectName);
            builder.append(resource);
            return builder.toString();
         }
      }
      builder.append("/resource/");
      builder.append(projectName);
      builder.append("/README.md");
      return builder.toString();
   }
}