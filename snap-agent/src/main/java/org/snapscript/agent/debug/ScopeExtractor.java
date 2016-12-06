package org.snapscript.agent.debug;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.snapscript.core.Context;
import org.snapscript.core.Scope;

public class ScopeExtractor implements ScopeBrowser {

   private final AtomicReference<String> evaluate;
   private final ScopeNodeTraverser traverser;
   private final AtomicInteger counter;
   private final Set<String> watch;
   private final Set<String> local;
   
   public ScopeExtractor(Context context, Scope scope) {
      this.traverser = new ScopeNodeTraverser(context, scope);
      this.evaluate = new AtomicReference<String>();
      this.watch = new CopyOnWriteArraySet<String>();
      this.local = new CopyOnWriteArraySet<String>();
      this.counter = new AtomicInteger();
   }
   
   public ScopeVariableTree build() {
      String expression = evaluate.get();
      Map<String, Map<String, String>> variables = traverser.expand(local);
      Map<String, Map<String, String>> evaluation = traverser.expand(watch, expression);
      int change = counter.get(); 
      
      return new ScopeVariableTree.Builder(change)
         .withLocal(variables)
         .withEvaluation(evaluation)
         .build();
   }
   
   @Override
   public void browse(Set<String> expand) {
      local.clear();
      local.addAll(expand);
      counter.getAndIncrement();
   }

   @Override
   public void evaluate(Set<String> expand, String expression) {
      watch.clear();
      watch.addAll(expand);
      evaluate.set(expression);
      counter.getAndIncrement();
   }
  
}
