package org.snapscript.develop.http.display;

import java.io.InputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;
import org.snapscript.develop.http.resource.FileResolver;
import org.snapscript.develop.http.resource.Resource;
import org.snapscript.develop.http.resource.ResourceMatcher;

public class DisplayResourceMatcher implements ResourceMatcher {

   private final DisplayContentProcessor displayProcessor;
   private final FileResolver fileResolver;
   private final ProcessLogger logger;
   
   public DisplayResourceMatcher(DisplayContentProcessor displayProcessor, FileResolver fileResolver, ProcessLogger logger) {
      this.displayProcessor = displayProcessor;
      this.fileResolver = fileResolver;
      this.logger = logger;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      InputStream stream = fileResolver.resolveStream(target);
      
      if(stream != null) {
         return new DisplayFileResource(displayProcessor, logger);
      }
      return null;
   }
}
