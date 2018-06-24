package org.snapscript.studio.service.project;

import static org.simpleframework.http.Protocol.CONTENT_DISPOSITION;
import static org.simpleframework.http.Protocol.DATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.studio.common.resource.Resource;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.Project;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/archive/.*")
public class ProjectArchiveResource implements Resource {

   private final Workspace workspace;

   public ProjectArchiveResource(Workspace workspace){
      this.workspace = workspace;
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      Path path = request.getPath();
      Project project = workspace.getProject(path);
      String mainScript = path.getPath(2); // /<project-name>/<project-path> or /default/blah.snap
      File archiveFile = project.getExportedArchive(mainScript);
      String name = archiveFile.getName();
      long time = System.currentTimeMillis();
    
      response.setStatus(Status.OK);
      response.setContentType("application/octet-stream");
      response.setValue(CONTENT_DISPOSITION, "attachment; filename=" + name + ";");
      response.setDate(DATE, time);
      
      OutputStream output = response.getOutputStream();
      InputStream source = new FileInputStream(archiveFile);
      
      try {
         IOUtils.copy(source, output);
      } finally {
         source.close();
         output.close();
      }
   }
}