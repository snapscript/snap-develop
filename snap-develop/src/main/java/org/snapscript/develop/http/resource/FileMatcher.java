package org.snapscript.develop.http.resource;

import java.io.InputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.agent.log.ProcessLogger;

public class FileMatcher implements ResourceMatcher {
   
   private final ContentTypeResolver typeResolver;
   private final FileResolver fileResolver;
   private final ProcessLogger logger;
   
   public FileMatcher(FileResolver fileResolver, ContentTypeResolver typeResolver, ProcessLogger logger) {
      this.fileResolver = fileResolver;
      this.typeResolver = typeResolver;
      this.logger = logger;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      InputStream stream = fileResolver.resolveStream(target);
      
      if(stream != null) {
         return new FileSystemResource(fileResolver, typeResolver, logger);
      }
      return null;
   }

}
