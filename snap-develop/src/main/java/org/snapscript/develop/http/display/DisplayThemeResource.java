/*
 * DisplayThemeResource.java December 2016
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

package org.snapscript.develop.http.display;

import java.io.PrintStream;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;
import org.snapscript.develop.http.resource.Resource;

import com.google.gson.Gson;

// /theme/<project>
public class DisplayThemeResource implements Resource {
   
   private final DisplayPersister displayPersister;
   private final Gson gson;
   
   public DisplayThemeResource(DisplayPersister displayPersister) {
      this.displayPersister = displayPersister;
      this.gson = new Gson();
   }

   @Override
   public void handle(Request request, Response response) throws Throwable {
      DisplayDefinition display = displayPersister.readDefinition();
      PrintStream out = response.getPrintStream();
      String text = gson.toJson(display);
      response.setStatus(Status.OK);
      response.setContentType("application/json");
      out.println(text);
      out.close();
   }

}
