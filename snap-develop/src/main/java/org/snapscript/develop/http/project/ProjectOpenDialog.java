/*
 * ProjectOpenDialog.java December 2016
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

package org.snapscript.develop.http.project;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.http.resource.Resource;
import org.snapscript.develop.http.resource.template.TemplateEngine;
import org.snapscript.develop.http.resource.template.TemplateModel;

public class ProjectOpenDialog implements Resource {
   
   private final ProjectBuilder builder;
   private final TemplateEngine engine;
   private final String resource;
   
   public ProjectOpenDialog(ProjectBuilder builder, TemplateEngine engine, String resource) {
      this.builder = builder;
      this.resource = resource;
      this.engine = engine;
   }
   
   @Override
   public void handle(Request request, Response response) throws Exception {
      Map<String, Object> map = new HashMap<String, Object>();
      TemplateModel model = new TemplateModel(map);
      File root = builder.getRoot();
      String name = root.getName();
      map.put("root", name);
      String text = engine.renderTemplate(model, resource);
      PrintStream stream = response.getPrintStream();

      response.setContentType("text/html");
      stream.print(text);
      stream.close();
   }
}
