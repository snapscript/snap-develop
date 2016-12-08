package org.snapscript.develop.command;

import java.util.Set;

public class EvaluateCommand implements Command {

   private Set<String> expand;
   private String expression;
   private String thread;
   private boolean refresh;
   
   public EvaluateCommand() {
      super();
   }
   
   public EvaluateCommand(String thread, String expression, boolean refresh, Set<String> expand) {
      this.expression = expression;
      this.thread = thread;
      this.refresh = refresh;
      this.expand = expand;
   }

   public boolean isRefresh() {
      return refresh;
   }

   public void setRefresh(boolean refresh) {
      this.refresh = refresh;
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
