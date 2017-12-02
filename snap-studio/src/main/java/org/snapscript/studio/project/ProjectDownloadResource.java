package org.snapscript.studio.project;

import org.snapscript.studio.common.resource.ContentTypeResolver;
import org.snapscript.studio.common.resource.ResourcePath;
import org.snapscript.studio.project.Workspace;
import org.springframework.stereotype.Component;

@Component
@ResourcePath("/download/.*")
public class ProjectDownloadResource extends ProjectFileResource {

   public ProjectDownloadResource(Workspace workspace, ContentTypeResolver resolver) {
      super(workspace, resolver);
   }
   
   @Override
   public boolean isDownload(){
      return true;
   }

}
