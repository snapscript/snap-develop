/*
 * ProfileResult.java December 2016
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

package org.snapscript.agent.profiler;

public class ProfileResult implements Comparable<ProfileResult>{
   
   private String resource;
   private Integer line;
   private Integer count;
   private Long time;
   
   public ProfileResult() {
      super();
   }
   
   public ProfileResult(String resource, Long time, Integer count, Integer line) {
      this.resource = resource;
      this.time = time;
      this.line = line;
      this.count = count;
   }
   
   @Override
   public int compareTo(ProfileResult other) {
      int compare = other.time.compareTo(time);
      
      if(compare == 0) {
         return other.line.compareTo(line);
      }
      return compare;
   }
   
   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public int getCount() {
      return count;
   }

   public void setCount(int count) {
      this.count = count;
   }

   public int getLine(){
      return line;
   }
   
   public void setLine(Integer line) {
      this.line = line;
   }
   
   public long getTime(){
      return time;
   }
   
   public void setTime(Long time) {
      this.time = time;
   }
}
