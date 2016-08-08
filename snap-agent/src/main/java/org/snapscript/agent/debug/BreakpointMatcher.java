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
         Set set = matches[line];
      
         if(set != null) {
            return set.contains(resource);
         }
      }
      return false;
   }
}
