/*
 * Comment.java December 2016
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
 * Comment.java March 2006
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

/**
 * The <code>Comment</code> object is used to parse a JSP comment. 
 * This performs a very simple parsing of the JSP comment provided.
 * This is parsed so that it can be included into the source.
 * <pre>
 *
 *    &lt;%-- comment --%&gt;
 * 
 * </pre>
 * The above expression is parsed an inserted into the JSP source
 * as an embedded comment starting with "/*" comment tag.
 * 
 * @author Niall Gallagher
 */ 
class Comment extends Token {

   /**
    * This is used to collect the token parsed from the print.
    */         
   private TokenBuffer comment;        
      
   /**
    * Constructor for the <code>Comment</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied print token.
    */   
   public Comment() {
      this.comment = new TokenBuffer();           
   }
   
   /**
    * Constructor for the <code>Comment</code> token. This will
    * create a buffer, which can be used to accumulate the data
    * extracted from the supplied print token before parsing.
    *
    * @param token this is the insert token to be parsed
    */    
   public Comment(String token) {
      this();
      parse(token);      
   }   

   /**
    * This method will supply code to the document definition that
    * will allow a comment to be printed by the page. The data
    * inserted into the definition will be displayed in the body.
    *
    * @param source this is the source to push the code into
    * @param builder this is the builder driving the process
    */ 
   public void process(Definition source, Builder builder){
      source.addContent("/*" + comment + "*/");
   }        

   /**
    * This will clear the comment token so that the parse can be reused
    * by the builder. In practice this method just satisfies the
    * contract of the token so that this object is not abstract.
    */     
   protected void init() {
      comment.clear();           
   }

   /**
    * This is a very simple parse method which basically extracts the
    * begining and end values from the token. For instance this will
    * remove "&lt;%--" and "--%&gt;" from the token supplied. 
    */ 
   protected void parse() {
      if(skip("<%--")) {
         while(off < count) {      
            if(skip("--%>")) {        
               break;
            }
            if(skip("%>")){
               break;                    
            }
            comment.append(buf[off++]);              
         }
      }           
   }
}
