package org.snapscript.develop.command;

import java.util.Set;

public class EvaluateCommand implements Command {

   private Set<String> expand;
   private String expression;
   private String thread;
   
   public EvaluateCommand() {
      super();
   }
   
   public EvaluateCommand(String thread, String expression, Set<String> expand) {
      this.thread = thread;
      this.expand = expand;
   }

   public Set<String> getExpand() {
      return expand;
   }

   public void setExpand(Set<String> expand) {
      this.expand = expand;
   }

   public String getExpression() {
      return expression;
   }

   public void setExpression(String expression) {
      this.expression = expression;
   }

   public String getThread() {
      return thread;
   }

   public void setThread(String thread) {
      this.thread = thread;
   }
}
