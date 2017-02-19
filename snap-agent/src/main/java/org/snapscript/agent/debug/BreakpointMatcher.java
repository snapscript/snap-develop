/*
 * BreakpointMatcher.java December 2016
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

package org.snapscript.agent.debug;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BreakpointMatcher {

   private volatile Set[] matches;
   
   public BreakpointMatcher() {
      this.matches = new Set[0];
   }
   
   public void update(Map<String, Map<Integer, Boolean>> breakpoints) {
      Set[] copy = new Set[1024];
      Set<String> resources = breakpoints.keySet();
      
      for(String resource : resources) {
         Map<Integer, Boolean> locations = breakpoints.get(resource);
         Set<Integer> lines = locations.keySet();
         
         for(Integer line : lines) {
            Boolean enabled = locations.get(line); 
                  
            if(enabled.booleanValue()) {
               if(line > copy.length) {
                  Set[] temp = new Set[line * 2];
                  
                  for(int i = 0; i < copy.length; i++) {
                     temp[i] = copy[i];
                  }
                  copy = temp;
               }
               Set set = copy[line];
               
               if(set == null) {
                  set = new HashSet();
                  copy[line] = set;
               }
               String module = ResourceExtractor.extractModule(resource);
               
               set.add(module); // add module 
               set.add(resource); // add module resource file
            }
         }
      }
      matches = copy;
   }
   
   public boolean match(String resource, int line) {
      if(line < matches.length) {
         if(line >= 0) {
            Set set = matches[line];
         
            if(set != null) {
               return set.contains(resource);
            }
         }
      }
      return false;
   }
}
