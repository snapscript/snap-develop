/*
 * TemplateMatcher.java December 2016
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

package org.snapscript.develop.resource.template;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.snapscript.develop.resource.Resource;
import org.snapscript.develop.resource.ResourceMatcher;

public class TemplateMatcher implements ResourceMatcher {
   
   private final Map<String, Method> cache;
   private final TemplateEngine engine;
   private final Object value;
   
   public TemplateMatcher(TemplateEngine engine, Object value) {
      this.cache = new ConcurrentHashMap<String, Method>();
      this.engine = engine;
      this.value = value;
   }

   @Override
   public Resource match(Request request, Response response) throws Exception {
      Path path = request.getPath();
      String[] segments = path.getSegments();
      
      if(cache.isEmpty()) {
         Class type = value.getClass();
         Method[] methods = type.getDeclaredMethods();
         
         for(Method method : methods) {
            Class returnType = method.getReturnType();
            
            if(returnType == TemplateResult.class) {
               Class[] parameterTypes = method.getParameterTypes();
               
               if(parameterTypes.length == 2) {
                  if(parameterTypes[0] == Request.class && parameterTypes[1] == Response.class) {
                     String name = method.getName();
                     cache.put(name, method);
                  }
               }
            }
            method.setAccessible(true);
         }
         
      }
      Method match = cache.get(segments[0]);
      
      if(match != null) {
         return new TemplateResource(engine);
      }
      return null;
   }
   
   private class TemplateResource implements Resource {
      
      private final TemplateEngine engine;
      
      public TemplateResource(TemplateEngine engine) {
         this.engine = engine;
      }

      @Override
      public void handle(Request request, Response response) throws Throwable {
         Path path = request.getPath();
         String[] segments = path.getSegments();
         Method method = cache.get(segments[1]);
         TemplateResult result = (TemplateResult)method.invoke(value, request, response);
         TemplateModel model = result.getModel();
         String template = result.getTemplate();
         PrintStream stream = response.getPrintStream();
         String text = engine.renderTemplate(model, template);
         
         response.setContentType("text/html");
         stream.print(text);
         stream.close();
      }

   }
}
