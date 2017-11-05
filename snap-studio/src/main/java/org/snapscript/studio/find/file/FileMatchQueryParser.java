package org.snapscript.studio.find.file;

import java.io.File;

import lombok.AllArgsConstructor;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.common.RequestParser;
import org.snapscript.studio.resource.project.Project;

@AllArgsConstructor
public class FileMatchQueryParser {

   private static final String EXPRESSION = "expression";
   
   private final Workspace workspace;
   
   public FileMatchQuery parse(Request request) {
      RequestParser parser = new RequestParser(request);
      Path path = request.getPath();
      Project project = workspace.getProject(path);
      
      if(project == null) {
         throw new IllegalStateException("Could not find project for " + path);
      }
      String name = project.getProjectName();
      File root = project.getProjectPath();
      String query = parser.getString(EXPRESSION);
      
      return FileMatchQuery.builder()
            .query(query)
            .path(root)
            .project(name)
            .build();
   }
}