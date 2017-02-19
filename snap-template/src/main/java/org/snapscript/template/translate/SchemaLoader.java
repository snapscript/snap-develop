/*
 * SchemaLoader.java December 2016
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

/*
 * SchemaLoader.java February 2006
 *
 * Copyright (C) 2006, Niall Gallagher <niallg@users.sf.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * Public License along with this library; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */

package org.snapscript.template.translate;

import freemarker.template.Template;
import freemarker.template.Configuration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import simple.page.Workspace;

/**
 * The <code>SchemaLoader</code> is used to load schemas which can
 * render a source file given a document definition. This will use a
 * properties file within the translation package to decide, based on
 * the runtime language, which template to load. By default this will
 * support the Groovy and Java runtime languages for JSP sources.
 * 
 * @author Niall Gallagher
 */ 
final class SchemaLoader extends Configuration {

   /**
    * Represents the language to template mappings for loading.
    */         
   private static ResourceBundle load;   
   
   static {      
      try { 
         load = ResourceBundle.getBundle("simple.page.translate.Schema");
      }catch(MissingResourceException e){
         e.printStackTrace();
      }   
   }
   
   /**
    * Constructor for the <code>SchemaLoader</code> object. This is
    * used to load schemas that can render sources for a runtime
    * language. By default this can load Groovy and Java schemas.
    *
    * @param project this is the workspace for the current project
    */ 
   public SchemaLoader(Workspace project) {
      this(project, SchemaLoader.class);           
   }
   
   /**
    * Constructor for the <code>SchemaLoader</code> object. This is
    * used to load schemas that can render sources for a runtime
    * language. By default this can load Groovy and Java schemas.
    *
    * @param project this is the workspace for the current project
    * @param source this is the class to load templates with
    */    
   public SchemaLoader(Workspace project, Class source)  {
      this.setClassForTemplateLoading(source, "/");           
   }  

   /**
    * This is used to load a schema for the runtime language that 
    * is specified by the provided source object. If the language is
    * not specified within the provided <code>Definition</code> then 
    * this will throw an exception.
    * 
    * @param source the source object that is to be rendered
    *
    * @return this returns a schema to render the source object
    */ 
   public Schema getSchema(Definition source) throws Exception {
      String type = source.getLanguage();

      try {
         type = load.getString(type);                  
      }catch(MissingResourceException e){
      }            
      return getInstance(source, type);
   }  

   /**
    * This is used to load a schema for the runtime language that 
    * is specified by the provided source object. The schema named
    * is loaded, if it cannot be located an exception is thrown.
    * 
    * @param source the source object that is to be rendered
    * @param type this is the name of the schema file to load
    *
    * @return this returns a schema to render the source object
    */    
   private Schema getInstance(Definition source, String type) throws Exception {
      return new Schema(getTemplate(type), source);           
   }   
}
