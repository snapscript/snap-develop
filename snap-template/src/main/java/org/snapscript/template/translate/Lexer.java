/*
 * Lexer.java December 2016
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
 * Lexer.java February 2006
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
 * The <code>Lexer</code> interface represents an object that can be
 * initialized with token matching patterns. This is used so that
 * a parser can be handed an arbitrary lexer implementation and be
 * able to tell that lexer the token types it would like to receive.
 * Typically this will be initialized with tokens such as those
 * used by PHP, JSP, and Ruby on Rails. For example, tokens such as
 * the JSP <code>&lt;%</code> and <code>%&gt;</code> could be used.
 * Such tokens would ensure the lexer emits JSP directives.
 * 
 * @author Niall Gallagher
 *
 * @see simple.page.translate.Tokenizer
 */
interface Lexer {  

   /**
    * This method tells the lexer how to extract the tokens
    * from the source document. This is given the opening and
    * closing tokens used to identify a segment. Typically
    * with languages such as JSP and PHP code segments are
    * opened with a delimeter like <code>&lt;%</code> for JSP
    * and <code>&lt;?php</code> for PHP. This method allows
    * the lexer to be configured to process such delimeters.
    *
    * @param start this is the opening token for a segment
    * @param finish this is the closing token for a segment
    */
   public void match(String start, String finish);

   /**
    * This method tells the lexer how to extract the tokens
    * from the source document. This is given the opening and
    * closing tokens used to identify a segment. Typically
    * with languages such as JSP and PHP code segments are
    * opened with a delimeter like <code>&lt;%</code> for JSP
    * and <code>&lt;?php</code> for PHP. This method allows
    * the lexer to be configured to process such delimeters.
    * <p>
    * With this <code>match</code> method a collection of
    * special characters can be specified. These characters
    * tell the lexer what it should allow whitespace to
    * surround for example take the HTML expressions below.
    * <pre>
    * 
    * &lt;   script language ='groovy' &gt;
    * &lt;script language='groovy'&gt;
    *
    * </pre>
    * The above two HTML expressions should be considered 
    * equals using the special characters <code>&lt;</code>,
    * <code>&gt;</code>, and <code>=</code>. 
    *
    * @param start this is the opening token for a segment
    * @param finish this is the closing token for a segment
    */  
   public void match(String start, String finish, String special);
}
