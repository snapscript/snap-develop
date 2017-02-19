/*
 * FilePatternMatcher.java December 2016
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilePatternMatcher {

   public static List<File> scan(Pattern pattern, File directory) throws Exception {
      List<File> files = new ArrayList<File>();
      
      if(directory.exists()) {
         File[] list = directory.listFiles();
         String normal = directory.getCanonicalPath();
         Matcher matcher = pattern.matcher(normal);
         
         if(matcher.matches()) {
            files.add(directory);
         } else {
            for(File entry : list) {
               normal = entry.getCanonicalPath();
               matcher = pattern.matcher(normal);
               
               if(matcher.matches()) {
                  if(entry.exists() && entry.isFile()) {
                     files.add(entry);
                  }
               }
               if(entry.isDirectory()) {
                  List<File> matches = scan(pattern, entry);
                  
                  if(!matches.isEmpty()) {
                     files.addAll(matches);
                  }
               }
            }
         }
      }
      return files;
   }
}
