package org.snapscript.develop.find.file;

import java.io.File;

import lombok.AllArgsConstructor;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.snapscript.develop.common.RequestParser;
import org.snapscript.develop.resource.project.Project;
import org.snapscript.develop.resource.project.ProjectBuilder;

@AllArgsConstructor
public class FileMatchQueryParser {

   private static final String EXPRESSION = "expression";
   
   private final ProjectBuilder builder;
   
   public FileMatchQuery parse(Request request) {
      RequestParser parser = new RequestParser(request);
      Path path = request.getPath();
      Project project = builder.getProject(path);
      
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