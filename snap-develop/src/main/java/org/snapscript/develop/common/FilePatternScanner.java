/*
 * FilePatternScanner.java December 2016
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

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FilePatternScanner {
   
   private static final String RECURSIVE_PATTERN = "_RECURSIVE_PATTERN_";
   private static final String SINGLE_PATTERN = "_SINGLE_PATTERN_";
   
   public static List<File> scan(String token) throws Exception {
      File file = new File(token);
      
      if(token.contains("*")) {
         int index = token.indexOf("*");
         String expression = token.trim();
         
         if(index != -1) {
            String parent = token.substring(0, index);
            File directory = new File(parent);
            
            if(directory.exists()) {
               expression = expression.replace("**", RECURSIVE_PATTERN); // convert \** to \.*
               expression = expression.replace("*", SINGLE_PATTERN); // convert \* to file regex
               
               File path = new File(expression);
               
               try {
                  expression = path.getCanonicalPath(); // remove ../ and ./
                  expression = PatternEscaper.escape(expression);
                  expression = expression.replace(RECURSIVE_PATTERN, ".*");
                  expression = expression.replace(SINGLE_PATTERN, "[a-zA-Z0-9_\\$\\-\\(\\)\\.\\s]+");
                  
                  Pattern pattern = Pattern.compile(expression);
                  List<File> list = FilePatternMatcher.scan(pattern, directory);
                  
                  Collections.sort(list);
                  return list;
               }catch(Exception e) {
                  throw new IllegalArgumentException("Could not parse pattern '" +token+ "'", e);
               }
            }
         }
      }
      if(file.exists()) {
         return Collections.singletonList(file);
      }
      return Collections.emptyList();
   }
}
