package org.snapscript.agent.debug;

import java.util.Map;

public class ScopeVariableTree {

   private final Map<String, Map<String, String>> variables;
   private final int change;
   
   public ScopeVariableTree(Map<String, Map<String, String>> variables, int change) {
      this.variables = variables;
      this.change = change;
   }
   
   public Map<String, Map<String, String>> getVariables() {
      return variables;
   }
   
   public int getChange() {
      return change;
   }
}
