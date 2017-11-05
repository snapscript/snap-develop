package org.snapscript.studio.resource.display;

import java.io.InputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.studio.Workspace;
import org.snapscript.studio.resource.Content;
import org.snapscript.studio.resource.FileResolver;
import org.snapscript.studio.resource.Resource;
import org.snapscript.studio.resource.ResourceMatcher;

public class DisplayResourceMatcher implements ResourceMatcher {

   private final DisplayContentProcessor displayProcessor;
   private final FileResolver fileResolver;
   private final Workspace workspace;
   
   public DisplayResourceMatcher(DisplayContentProcessor displayProcessor, FileResolver fileResolver, Workspace workspace) {
      this.displayProcessor = displayProcessor;
      this.fileResolver = fileResolver;
      this.workspace = workspace;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      Content content = fileResolver.resolveContent(target);
      InputStream stream = content.getInputStream();
      
      if(stream != null) {
         return new DisplayFileResource(displayProcessor, workspace);
      }
      return null;
   }
}