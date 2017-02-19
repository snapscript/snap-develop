/*
 * Include.java December 2016
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
 * Include.java February 2006
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

/**
 * The <code>Include</code> object is used to process JSP includes.
 * This require recursive processing of the document, as its source
 * lies in seperate files. The following token is processed.
 * <pre>
 *
 *    &lt;%@ include file="path" %&gt;
 * 
 * </pre>
 * This above token requires that the "path" refers to a file that
 * exists relative to the parent JSP source. For example if the
 * parent was "/path/Parent.jsp" and the file parameter was the
 * path "../File.jsp" then "/File.jsp" is the sourc evaluated.
 *
 * @author Niall Gallagher
 */ 
class Include extends Token {

   /**
    * This is used to accumulate the bytes for the file path.
    */ 
   private TokenBuffer include;        

   /**
    * Constructor for the <code>Include</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied include token.
    */ 
   public Include() {
      this.include = new TokenBuffer();            
   }
   
   /**
    * Constructor for the <code>Include</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied insert token before parsing.
    *
    * @param token this is the include token to be parsed
    */  
   public Include(String token) {
      this();
      parse(token);
   }

   /**
    * This will use the extracted path from the include token to 
    * recursively process the document definition. This basically
    * uses asks the builder to build and the evaluate the named
    * file, if the file does not exist an exception is throws.
    *
    * @param source this is the document definition processed
    * @param builder this is used for recursive JSP processing
    */ 
   public void process(Definition source, Builder builder) throws IOException {
      builder.build(include.text(), source);
   }        

   /**
    * This will clear the name token so that the parse can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */    
   protected void init() {
      include.clear();               
   }
   
   /**
    * This does not verify the token type, instead this will seek 
    * the '=' character. Once the '=' character has been encountered
    * the file is extracted as a quoted string, for example "path".
    */ 
   protected void parse() {
      scrap();
      include();      
   }

   /**
    * This is a quick and dirty means of parsing the token. This 
    * will basically seek the '=' character such the offset of
    * the buffer is on the start of the name, like "[p]ath".
    */ 
   private void scrap() {
      while(off < count){
         char next = buf[off];
         
         if(next == '='){
            while(off < count) {
               next = buf[off++];                                 
               if(quote(next)) {
                  break;
               }              
            }
            break;            
         }        
         off++;      
      }           
   }
   
   /**
    * This method will read all characters up to a space or the
    * next quotation chatacter, for example "'" or '"' will be
    * considered a terminal. Once this has finished the name will
    * be stored in the internal name buffer for processing.
    */ 
   private void include() {
      while(off < count) {
         char next = buf[off++];              

         if(space(next)) {
            break;                 
         }else if(quote(next)){
            break;                 
         }
         include.append(next);
      }           
   }

   /**
    * This is used to determine when the start and end of the name
    * has been encountered. The terminals are '"' and '"', which 
    * are legal quotations within the JSP syntax.
    *
    * @param ch this is the character to be evaluated
    *
    * @return this returns true if the character is a quote
    */     
   private boolean quote(char ch) {
      return ch == '"' || ch == '\'';
   }   
}
