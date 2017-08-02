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