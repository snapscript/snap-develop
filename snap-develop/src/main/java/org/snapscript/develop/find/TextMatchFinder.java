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
   
   private final ProcessLogger logger;
   
   public TextMatchFinder(ProcessLogger logger) {
      this.logger = logger;
   }

   public List<TextMatch> findText(File file, String project, String resource, String expression) {
      try {
         List<TextMatch> lines = new ArrayList<TextMatch>();
         FileReader source = new FileReader(file);
         LineNumberReader reader = new LineNumberReader(source);

         try {
            String token = expression.toLowerCase();
            
            while(reader.ready()) {
               String line = reader.readLine();
               
               if(line == null) {
                  break;
               }
               if(line.toLowerCase().contains(token)) {
                  int number = reader.getLineNumber();
                  String text = line.replace(expression, "<span style='background-color: #f0f0f0;'>"+expression+"</span>");
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
