package org.snapscript.develop.http.resource;

import java.io.InputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class FileMatcher implements ResourceMatcher {
   
   private final ContentTypeResolver typeResolver;
   private final FileResolver fileResolver;
   
   public FileMatcher(FileResolver fileResolver, ContentTypeResolver typeResolver) {
      this.fileResolver = fileResolver;
      this.typeResolver = typeResolver;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      InputStream stream = fileResolver.resolveStream(target);
      
      if(stream != null) {
         return new FileSystemResource(fileResolver, typeResolver);
      }
      return null;
   }

}
