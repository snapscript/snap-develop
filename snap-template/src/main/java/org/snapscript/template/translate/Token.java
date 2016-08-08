/*
 * Token.java February 2006
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

import java.io.IOException;

import org.snapscript.parse.StringParser;

/**
 * The <code>Token</code> object is used to parse a token from the
 * JSP source. This will parse a single token and push the data
 * into the document definition. 
 *
 * @author Niall Gallagher
 */ 
abstract class Token extends StringParser {

   /**
    * This will process the data parsed from the token. This method
    * is used to push data into the document definition. For example
    * imports could be pushed into the definition for generation.
    *
    * @param source this is the definition to be populated
    * @param builder this is used for recursive JSP processing
    */         
   public abstract void process(Definition source, Builder builder) throws IOException;
   
}
