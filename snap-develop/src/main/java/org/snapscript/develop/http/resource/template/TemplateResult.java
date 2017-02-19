/*
 * TemplateResult.java December 2016
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

package org.snapscript.develop.http.resource.template;

import java.util.Collections;
import java.util.Map;

public class TemplateResult {
   
   private final TemplateModel model;
   private final String template;
   
   public TemplateResult(String template) {
      this(template, Collections.EMPTY_MAP);
   }
   
   public TemplateResult(String template, Map<String, Object> values) {
      this.model = new TemplateModel(values);
      this.template = template;
   }
   
   public String getTemplate() {
      return template;
   }
   
   public TemplateModel getModel() {
      return model;
   }
}
