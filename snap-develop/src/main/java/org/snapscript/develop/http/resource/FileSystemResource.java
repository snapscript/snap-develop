/*
 * FileSystemResource.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

package org.snapscript.develop.http.resource;

import static org.simpleframework.http.Protocol.CONTENT_ENCODING;
import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Status.OK;

import java.io.OutputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.agent.log.ProcessLogger;

public class FileSystemResource implements Resource {

   private final FileContentCompressor fileCompressor;
   private final ProcessLogger logger;
   private final Status status;

   public FileSystemResource(FileResolver fileResolver, ContentTypeResolver typeResolver, ProcessLogger logger) {
      this(fileResolver, typeResolver, logger, OK);
   }

   public FileSystemResource(FileResolver fileResolver, ContentTypeResolver typeResolver, ProcessLogger logger, Status status) {
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
