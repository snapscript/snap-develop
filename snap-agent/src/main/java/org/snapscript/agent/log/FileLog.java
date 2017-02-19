/*
 * FileLog.java December 2016
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
import java.util.concurrent.atomic.AtomicInteger;

public class FileLog implements ProcessLog{
  
   private final FileLogCompressor compressor;
   private final FileLogAppender appender;
   private final FileLogRoller roller;
   private final AtomicInteger counter;
   private final ConsoleLog logger;
   private final File file;
   private final int lines;
   
   public FileLog(File file) {
      this(file, true);
   }
   
   public FileLog(File file, boolean append) {
      this(file, append, 20000);
   }
   
   public FileLog(File file, boolean append, int lines) {
      this.counter = new AtomicInteger(lines > 0 ? lines : 1000);
      this.appender = new FileLogAppender(file, append);
      this.roller = new FileLogRoller();
      this.compressor = new FileLogCompressor(roller);
      this.logger = new ConsoleLog();
      this.lines = lines;
      this.file = file;
   }

   @Override
   public void log(Object text) {
      log(text, null);
   }

   @Override
   public void log(Object text, Throwable cause) {
      try {
         File normal = file.getCanonicalFile();
         File parent = normal.getParentFile();
         int count = counter.getAndDecrement();
         
         if(!parent.exists()) {
            parent.mkdirs();
         }
         if(count == lines) { // roll on first time
            compressor.compressFiles(parent); // compress the old files
         }
         if(count <= 0) { // roll when capacity exceeds
            counter.set(lines > 0 ? lines : 1000);
            appender.close();
            roller.rollFile(file); // does this mean append never happens
            compressor.compressFiles(parent); // compress the old files
         }
         logger.log(text, cause); // log to console also
         appender.append(text, cause);
      }catch(Exception e) {
         logger.log("Could not append to " + file, e);
         appender.close();
      }
   }

}
