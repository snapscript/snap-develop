package org.snapscript.studio.common.find.file;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.snapscript.studio.common.FileDirectory;
import org.snapscript.studio.common.FileDirectorySource;

@Path("/file")
public class FileMatchResource {

   private final FileDirectorySource workspace;
   private final FileMatchScanner scanner;
   
   @Inject
   public FileMatchResource(FileDirectorySource workspace) {
      this.scanner = new FileMatchScanner();
      this.workspace = workspace;
   }
   
   @GET
   @Produces("application/json")
   @Path("/{project}")
   public List<FileMatch> findFiles(
         @PathParam("project") String name, 
         @QueryParam("expression") String expression) throws Exception 
   {
      FileDirectory project = workspace.getProject(name);
      File directory = project.getProjectPath();
      
      return scanner.findAllFiles(directory, name, expression);
   }
}