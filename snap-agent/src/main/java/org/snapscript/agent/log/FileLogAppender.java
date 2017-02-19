/*
 * FileLogAppender.java December 2016
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

package org.snapscript.agent.log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileLogAppender {

   private FileWriter appender;
   private PrintWriter writer;
   private File file;
   private boolean append;
   
   public FileLogAppender(File file) {
      this(file, true);
   }
   
   public FileLogAppender(File file, boolean append) {
      this.append = append;
      this.file = file;
   }
   
   public void append(Object text) {
      append(text, null);
   }
   
   public void append(Object text, Throwable cause) {
      try {
         if(!file.exists() || writer == null || writer.checkError()) {
            appender = new FileWriter(file, append);
            writer = new PrintWriter(appender);
         }
         writer.print(text);
        
         if(cause != null) {
            writer.print(": ");
            cause.printStackTrace(writer);
         } else {
            writer.println();
         }
         writer.flush();
      }catch(Exception e) {
         throw new IllegalStateException("Could not write to file '" + file + "'", e);
      }
   }
   
   public void close() {
      try {
         if(writer != null) {
            writer.flush();
            writer.close();
            writer = null;
         }
      }catch(Exception e) {
         throw new IllegalStateException("Could not close file '" + file + "'", e);
      }
   }
}
