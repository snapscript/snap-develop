package org.snapscript.agent.event;

import java.util.Set;

import org.snapscript.agent.profiler.ProfileResult;

public class ProfileEvent implements ProcessEvent {

   private Set<ProfileResult> results;
   private String process;
   
   public ProfileEvent(String process, Set<ProfileResult> results) {
      this.process = process;
      this.results = results;
   }

   @Override
   public String getProcess() {
      return process;
   }
   
   public Set<ProfileResult> getResults() {
      return results;
   }
   
   
}
