package org.snapscript.develop.http.resource;

import static org.simpleframework.http.Protocol.CONTENT_ENCODING;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Status.OK;

import java.io.OutputStream;

import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.agent.ConsoleLogger;

public class FileSystemResource implements Resource {

   private final FileContentCompressor fileCompressor;
   private final ConsoleLogger logger;
   private final Status status;

   public FileSystemResource(FileResolver fileResolver, ContentTypeResolver typeResolver, ConsoleLogger logger) {
      this(fileResolver, typeResolver, logger, OK);
   }

   public FileSystemResource(FileResolver fileResolver, ContentTypeResolver typeResolver, ConsoleLogger logger, Status status) {
      this.fileCompressor = new FileContentCompressor(fileResolver, typeResolver);
      this.logger = logger;
      this.status = status;
   }

   @Override
   public void handle(Request request, Response response) throws Exception {
      FileContent content = fileCompressor.compress(request);
      OutputStream output = response.getOutputStream();
      String type = content.getType();
      String path = content.getPath();
      String encoding = content.getEncoding();
      byte[] data = content.getData();
      double ratio = content.getCompression();
      long time = content.getDuration();
      
      logger.debug(path + " ratio=" + ratio + "% time=" + time + "ms");
      
      response.setCode(status.code);
      response.setDescription(status.description);
      response.setValue(CONTENT_TYPE, type);
      
      if(encoding != null){
         response.setValue(CONTENT_ENCODING, encoding);
      }
      response.setContentLength(data.length);
      output.write(data);
      output.close();
   }
}
