package org.snapscript.develop.http.resource;

import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Status.OK;

import java.io.InputStream;
import java.io.OutputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

public class FileSystemResource implements Resource {

   private final ContentTypeResolver typeResolver;
   private final FileResolver fileResolver;
   private final Status status;

   public FileSystemResource(FileResolver fileResolver, ContentTypeResolver typeResolver) {
      this(fileResolver, typeResolver, OK);
   }

   public FileSystemResource(FileResolver fileResolver, ContentTypeResolver typeResolver, Status status) {
      this.fileResolver = fileResolver;
      this.typeResolver = typeResolver;
      this.status = status;
   }

   @Override
   public void handle(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      String type = typeResolver.resolveType(target);
      InputStream input = fileResolver.resolveStream(target);
      OutputStream output = response.getOutputStream();

      response.setCode(status.code);
      response.setDescription(status.description);
      response.setValue(CONTENT_TYPE, type);
      
      byte[] block = new byte[1024];
      int count = 0;
      
      while((count = input.read(block)) != -1) {
         output.write(block, 0, count);
      }
      input.close();
      output.close();
   }
}
