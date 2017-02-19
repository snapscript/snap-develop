/*
 * Generator.java December 2016
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
 * Generator.java March 2006
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

import freemarker.log.Logger;
import simple.page.Workspace;
import java.io.FileWriter;
import java.io.File;

/**
 * The <code>Generator</code> object is used to generate the sources 
 * for the document definition built during parsing. This will acquire
 * a template suitable for the runtime language specified by the JSP
 * source. This templates is given the definition, and the output of
 * the template is written to source file specified by the definition.
 *
 * @author Niall Gallagher
 */ 
final class Generator {

   /**
    * To ensure that logging does not cause undesired JSP output, 
    * the freemarker logging is turned off for this generator.
    */ 
   static {
      try {           
         Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);           
      }catch(Exception e) {
         e.printStackTrace();              
      }         
   }        

   /**
    * This is used to load the template to match the source object.
    */        
   private SchemaLoader loader;

   /**
    * Constructor for the <code>Generator</code> object. This is used
    * to generate a source file for the specified runtime language.
    * By default the Java and Groovy languages can be generated.
    *
    * @param project this is the project used by this generator
    */ 
   public Generator(Workspace project) {
      this.loader = new SchemaLoader(project);
   }

   /**
    * This is used to generate the source for the provided document
    * definition. This will ensure that the source is written to the
    * file suggested by the document definition. If the directory
    * leading to the source file does not exist then it is created.
    *
    * @param source this is the document definition to generate
    */ 
   public void generate(Definition source) throws Exception {
      File path = source.getDirectory();
      File file = source.getSource();

      if(!path.exists()) {
         path.mkdirs();              
      }
      generate(source, file);
   }

   /**
    * This is used to generate the source for the provided document
    * definition. This will ensure that the source is written to the
    * file suggested by the document definition. If the directory
    * leading to the source file does not exist then it is created.
    *
    * @param source this is the document definition to generate
    */ 
   private void generate(Definition source, File file) throws Exception {
      Schema template = loader.getSchema(source);
      FileWriter data = new FileWriter(file);
   
      template.write(data);
      data.close();
   }   
}
