/*
 * ProblemFinder.java December 2016
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

package org.snapscript.develop.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.core.Reserved;
import org.snapscript.parse.SyntaxCompiler;
import org.snapscript.parse.SyntaxNode;
import org.snapscript.parse.SyntaxParser;

public class ProblemFinder {

   private final SyntaxCompiler compiler;
   
   public ProblemFinder() {
      this.compiler = new SyntaxCompiler();
   }
   
   public Problem parse(String project, String resource, String source) {
      try {
         String name = resource.toLowerCase();
         
         if(name.endsWith(Reserved.SCRIPT_EXTENSION)) {
            SyntaxParser parser = compiler.compile();
            SyntaxNode node = parser.parse(resource, source, "script");
            node.getNodes();
         }
      }catch(Exception cause) {
         String message = cause.getMessage();
         Pattern pattern = Pattern.compile(".*line\\s+(\\d+)");
         Matcher matcher = pattern.matcher(message);
         
         if(matcher.matches()) {
            String match = matcher.group(1);
            int line = Integer.parseInt(match);
            
            return new Problem(project, resource, message, line);
         }
         return new Problem(project, resource, message, 1);
      }
      return null;
   }
}
