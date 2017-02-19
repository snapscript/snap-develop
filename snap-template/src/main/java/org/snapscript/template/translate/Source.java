/*
 * Source.java December 2016
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
 * Source.java February 2006
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

import java.io.File;

/**
 * The <code>Source</code> object is used to describe a translated JSP
 * page. It contains information such as the language th JSP has been
 * written in, for example Java or Groovy, as well as a description of
 * the object and package that will result from compilation. Of core
 * importance is the <code>Reference</code> object provided. This
 * allows the JSP engine to determine when a JSP needs recompilation.
 * 
 * @author Niall Gallagher
 *
 * @see simple.page.translate.Reference
 */ 
public interface Source {

   /**
    * The language property is used to specify the type of source that
    * is to be generated and the compiler required to process that
    * source. The language property is specified within the JSP page
    * using the "page" directives "language" attribute. 
    *
    * @return this returns the language, for example Groovy or Java
    */ 
   public String getLanguage();

   /**
    * The provides the name of the resulting object. This is used by
    * the code generator to determine the correct class name and
    * constructor to use, and may be used by the source compiler.
    *
    * @return this returns the name of the source object generated
    */ 
   public String getName();

   /**
    * Provides the fully qualified package name for the resulting
    * object. This is required by the compiler, so that once the
    * generated source has been compiled it can later be loaded as a
    * class by the compiler class loader and instantiated. 
    *
    * @return this returns the fully qualified class name targeted
    */ 
   public String getTarget();

   /**
    * This is used by the source code generator to determine the
    * package the target object is using. This ensures that a correct
    * name space is given to each source, which avoids collisions.
    *
    * @return this returns the package name for the source object
    */ 
   public String getPackage();
           
   /**
    * This acquires the directory the source object is generated into.
    * This allows the generator to create the appropriate directory,
    * before generating the resulting Java or Groovy object into that
    * directory before compilation can proceed. 
    *
    * @return the directory to generate the source object into
    */ 
   public File getDirectory();

   /**
    * Provides the OS file system reference for the source file. This
    * can be used to acquire a file system location using the provided
    * project <code>Workspace</code>. This is used by the source
    * generator to determine where the resulting source is written to.
    *
    * @return this returns the file system path for the source
    */ 
   public File getSource();

   /**
    * This provides a key component for the JSP engine, which is used
    * to determine when a JSP page or its includes have expired. This
    * allows background compilation, and ensures that pages can be
    * edited and deployed without restarting the server.
    *
    * @return this returns a monitor to the referenced JSP sources
    */ 
   public Reference getReference();     
}
