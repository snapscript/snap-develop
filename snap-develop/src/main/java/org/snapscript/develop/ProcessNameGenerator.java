/*
 * ProcessNameGenerator.java December 2016
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

package org.snapscript.develop;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

public class ProcessNameGenerator implements ProcessNameFilter {
   
   private static final String PROCESS_PREFIX = "agent-";
   private static final String DATE_FORMAT = "ddHHmmss";
   private static final String PROCESS_PATTERN = PROCESS_PREFIX + "\\d+";
   
   private final AtomicLong counter;
   private final DateFormat format;
   
   public ProcessNameGenerator(){
      this.format = new SimpleDateFormat(DATE_FORMAT);
      this.counter = new AtomicLong(1);
   }
   
   public synchronized String generate() {
      long time = System.currentTimeMillis();
      long sequence = counter.getAndIncrement();
      String date = format.format(time);
      
      return String.format("%s%s%s", PROCESS_PREFIX, sequence, date);
   }

   @Override
   public synchronized boolean accept(String name) {
      if(name != null) {
         return name.matches(PROCESS_PATTERN);
      }
      return false;
   }
}
