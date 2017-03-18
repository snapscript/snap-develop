/*
 * ChainTemplateEngine.java December 2016
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

import java.util.List;

public class ChainTemplateEngine implements TemplateEngine {
   
   private final List<TemplateEngine> engines;
   
   public ChainTemplateEngine(List<TemplateEngine> engines) {
      this.engines = engines;
   }

   @Override
   public String renderTemplate(TemplateModel model, String template) throws Exception {
      for(TemplateEngine engine : engines) {
         if(engine.validTemplate(template)) {           
            return engine.renderTemplate(model, template);
         }
      }
      return null;
   }

   @Override
   public boolean validTemplate(String template) throws Exception {
      for(TemplateEngine engine : engines) {
         if(engine.validTemplate(template)) {           
            return true;
         }
      }
      return false;
   }

}
