/*
 * FileResource.java December 2016
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

import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Status.OK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

public class FileResource implements Resource {

   private final FileManager manager;
   private final Status status;
   private final String type;
   private final String file;

   public FileResource(FileManager manager, String file, String type) {
      this(manager, file, type, OK);
   }

   public FileResource(FileManager manager, String file, String type, Status status) {
      this.manager = manager;
      this.status = status;
      this.type = type;
      this.file = file;
   }

   @Override
   public void handle(Request request, Response response) throws IOException {
      OutputStream output = response.getOutputStream();
      InputStream input = manager.openInputStream(file);

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
