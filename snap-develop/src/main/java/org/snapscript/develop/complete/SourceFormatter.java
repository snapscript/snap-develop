/*
 * SourceFormatter.java December 2016
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

package org.snapscript.develop.complete;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snapscript.develop.http.project.Project;

public class SourceFormatter {

   private final Pattern pattern;
   
   public SourceFormatter(){
      this.pattern = Pattern.compile("^(\\s+)(.*)$");
   }
   
   public String format(Project project, String source, int indent) throws Exception {
      String lines[] = source.split("\\r?\\n");
      String pad = "";
      
      for(int i = 0; i < indent; i++) {
         pad += " ";
      }
      if(lines.length > 0){
         StringBuilder builder = new StringBuilder();
         
         for(String line : lines) {
            Matcher matcher = pattern.matcher(line);
            
            if(matcher.matches()) {
               String prefix = matcher.group(1);
               String remainder = matcher.group(2);
               float length = prefix.length();
               float factor = length / indent;
               
               if(length > 0) {
                  int count = Math.round(factor);
                  
                  for(int i = 0; i < count; i++) {
                     builder.append(pad);
                  }
                  builder.append(remainder);
               } else {
                  builder.append(remainder);
               }
            }else {
               builder.append(line);
            }
            builder.append("\n");
         }
         return builder.toString();
      }
      return source;
   }
}
