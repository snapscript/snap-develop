package org.snapscript.develop.http.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import org.simpleframework.http.Path;
import org.simpleframework.http.Protocol;
import org.simpleframework.http.Request;

public class FileContentCompressor {
   
   private static final String GZIP = "gzip";
   private static final String TYPE = "text";

   private final ContentTypeResolver typeResolver;
   private final FileResolver fileResolver;

   public FileContentCompressor(FileResolver fileResolver, ContentTypeResolver typeResolver) {
      this.fileResolver = fileResolver;
      this.typeResolver = typeResolver;
   }

   public FileContent compress(Request request) throws Exception {
      Path path = request.getPath();
      String target = path.getPath();
      String type = typeResolver.resolveType(target);
      InputStream input = fileResolver.resolveStream(target);
      String accept = request.getValue(Protocol.ACCEPT_ENCODING);
      long start = System.currentTimeMillis();
      
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         OutputStream output = buffer;
         String encoding = null;
         double original = 0.0;
         
         if(accept.contains(GZIP) && type.startsWith(TYPE)) { // only compress text
            output = new GZIPOutputStream(buffer);
            encoding = GZIP;
         }
         byte[] block = new byte[8192];
         int count = 0;
         
         while((count = input.read(block)) != -1) {
            output.write(block, 0, count);
            original += count;
         }
         output.close();
         input.close();
         byte[] data = buffer.toByteArray();
         long finish = System.currentTimeMillis();
         double ratio = (data.length / original) * 100.0; // 2 / 10
         int percentage = (int)ratio;
         
         return new FileContent(target, type, encoding, data, finish - start, percentage);
      } catch(Exception e) {
         throw new IllegalStateException("Could not compress " + target, e);
      }

   }
}
