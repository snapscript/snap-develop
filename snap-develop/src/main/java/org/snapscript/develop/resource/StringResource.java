/*
 * StringResource.java December 2016
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

package org.snapscript.develop.resource;

import static org.simpleframework.http.Protocol.CONTENT_TYPE;
import static org.simpleframework.http.Status.OK;

import java.io.OutputStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

public class StringResource implements Resource {

   private final String encoding;
   private final Status status;
   private final String content;
   private final String type;

   public StringResource(String content, String type, String encoding) {
      this(content, type, encoding, OK);
   }

   public StringResource(String content, String type, String encoding, Status status) {
      this.encoding = encoding;
      this.content = content;
      this.status = status;
      this.type = type;
   }

   @Override
   public void handle(Request request, Response response) throws Exception {
      OutputStream output = response.getOutputStream();
      long length = content.length();
      byte[] data = content.getBytes(encoding);

      response.setCode(status.code);
      response.setDescription(status.description);
      response.setValue(CONTENT_TYPE, type);
      response.setContentLength(length);
      output.write(data);
      output.close();
   }

}
