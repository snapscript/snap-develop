/*
 * Text.java December 2016
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
 * Text.java February 2006
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

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Text</code> token is used to parse plain text from the
 * JSP source. This will basically break the provided token up into
 * individual lines and push print statements into the definition.
 * Ther is no specific syntax for the text token, it is simply text.
 *
 * @author Niall Gallagher
 */ 
class Text extends Token {

   /**
    * This is used to collect the data parsed from the lines.
    */          
   private StringBuffer line;
        
   /**
    * This is used to collect the lines parsed from the text.
    */     
   private List list;

   /**
    * Constructor for the <code>Text</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied text token.
    */ 
   public Text() {
      this.line = new StringBuffer();
      this.list = new ArrayList();      
   }   
   
   /**
    * Constructor for the <code>Text</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied text token before parsing.
    *
    * @param token this is the text token to be parsed
    */ 
   public Text(String token) {
      this();
      parse(token);
   }

   /**
    * This is used to push the lines from the text token into the
    * document definition. This will iterate over the lines and add
    * them in the order they were encounterd within the source text.
    *
    * @param source this is the document definition to populate
    * @param builder this is the builder driving the process
    */ 
   public void process(Definition source, Builder builder) {
      for(int i = 0; i < list.size(); i++){
         source.addContent("out.print(\""+list.get(i)+"\");");
      }           
   }
   
   /**
    * This will clear the line token so that the parse can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */  
   protected void init() {
      line.setLength(0);
      list.clear();      
   }
   
   /**
    * This method is used to break the text up into individual lines
    * using the CR or LF to delimit the end of the line. Once all
    * lines have be extracted from the text this object will contain
    * a list of lines equivelant to the text extracted.
    */      
   protected void parse() {
      while(off < count) {
         line();
         insert();
      }
   }  

   /**
    * This will seek a CR or LF from the text token. Once either a
    * CR, '\r', or LF, '\n', has been extracted it is stored within
    * the line buffer and inserted into the list of lines.
    */ 
   private void line() {
      while(off < count) {
         char next = buf[off++];
         
         if(next == '"'){
            line.append("\\\"");  
         } else if(next == '\\'){
            line.append("\\\\");     
         } else if(next == '\r'){
            line.append("\\r");
            break;
         } else if(next == '\n'){
            line.append("\\n");
            break;
         } else {
            line.append(next);
         }
      }    
   }

   /**
    * This will insert the line buffer into the list of lines for the
    * text. A line is entered only if that line is greater than zero,
    * once inserted into the list the line buffer is cleared.
    */ 
   private void insert() {
      if(line.length() >0){
         list.add(line.text());
         line.clear();
      }
   }
}
