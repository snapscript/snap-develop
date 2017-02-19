/*
 * FileMatcher.java December 2016
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
