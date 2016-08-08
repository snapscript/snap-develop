/*
 * Translator.java February 2006
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

import simple.page.Workspace;

/**
 * The <code>Translator</code> object is used to translate a JSP source
 * file to a Java or Groovy source file. The translation process can be
 * brokwn up into two phases, these are the build phase and the 
 * generation phase. In the build phase the document is parsed and a
 * document definition is created as a <code>Definition</code> object.
 * <p>
 * The generation pahse uses the document definition constructed by the
 * build phase to generate the Java or Groovy source required to
 * compile the page into a usable, loadable object.,
 *
 * @author Niall Gallagher
 *
 * @see simple.page.compile.Compiler
 */ 
public class Translator {

   /**
    * This is used to generate a source file from the definition.
    */          
   private Generator generator;

   /**
    * This is used to build the document definition from the JSP.
    */ 
   private Builder builder;

   /**
    * Constructor for the <code>Translator</code> object. This needs
    * a workspace configuration in order to gather and compile the
    * JSP sources. All JSP sources will be gathered from the workspace
    * and written to a build path provided by the workspace.
    *
    * @param project this provides a workspace for translation
    */ 
   public Translator(Workspace project) {
      this.generator = new Generator(project);
      this.builder = new Builder(project);           
   }        

   /**
    * This method performs the translation of the JSP source file. The
    * source file is translated as part of a two phase process. First
    * the JSP file is build into a collection or code segments. Then
    * the source is generated in the runtime language required. The
    * possible runtime languages are Groovy and Java.
    *
    * @param target this is the JSP source file to be translated
    */ 
   public Source translate(String target) throws Exception {
      Definition source = builder.build(target);           
      generator.generate(source);
      return source;
   }
}
