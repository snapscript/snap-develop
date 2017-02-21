/*
 * TextMatchFinder.java December 2016
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

package org.snapscript.develop.find;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.snapscript.agent.log.ProcessLogger;

public class TextMatchFinder {
   
   private static final String FOREGROUND_COLOR = "#ffffff";
   private static final String BACKGROUND_COLOR = "#6495ed"; 
   
   private final ProcessLogger logger;
   
   public TextMatchFinder(ProcessLogger logger) {
      this.logger = logger;
   }

   public List<TextMatch> findText(TextFile textFile, String expression) {
      File file = textFile.getFile();
      String project = textFile.getProject();
      String resource = textFile.getPath();
      
      try {
         List<TextMatch> lines = new ArrayList<TextMatch>();
         FileReader source = new FileReader(file);
         LineNumberReader reader = new LineNumberReader(source);
         LineMatcher matcher = new LineMatcher(expression, BACKGROUND_COLOR, FOREGROUND_COLOR, true);
         
         try {
            while(reader.ready()) {
               String line = reader.readLine();
               
               if(line == null) {
                  break;
               }
               String text = matcher.match(line);
               
               if(text != null) {
                  int number = reader.getLineNumber();
                  TextMatch match = new TextMatch(project, resource, text, number);
                  lines.add(match);
               }
            }
         } finally {
            reader.close();
         }
         return lines;
      }catch(Exception e) {
         logger.debug("Could not read file", e);
      }
      return Collections.emptyList();
   }
}
