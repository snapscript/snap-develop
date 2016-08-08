package org.snapscript.develop.command;

import java.util.Set;

import org.snapscript.agent.profiler.ProfileResult;

public class ProfileCommand implements Command {
   
   private Set<ProfileResult> results;
   private String process;
   
   public ProfileCommand() {
      super();
   }
   
   public ProfileCommand(String process, Set<ProfileResult> results) {
      this.process = process;
      this.results = results;
   }
   
   public String getProcess() {
      return process;
   }
   
   public void setProcess(String process) {
      this.process = process;
   }
   
   public Set<ProfileResult> getResults() {
      return results;
   }
   
   public void setResults(Set<ProfileResult> results) {
      this.results = results;
   }

}
