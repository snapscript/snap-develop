package org.snapscript.agent.debug;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import org.snapscript.core.Context;
import org.snapscript.core.Scope;

public class ScopeExtractor implements ScopeBrowser {

   private final ScopeNodeTraverser traverser;
   private final AtomicInteger counter;
   private final Set<String> paths;
   
   public ScopeExtractor(Context context, Scope scope) {
      this.traverser = new ScopeNodeTraverser(context, scope);
      this.paths = new CopyOnWriteArraySet<String>();
      this.counter = new AtomicInteger();
   }
   
   public ScopeVariableTree build() {
      Map<String, Map<String, String>> variables = traverser.expand(paths);
      int change = counter.get(); 
      
      return new ScopeVariableTree(variables, change);
   }
   
   @Override
   public void browse(Set<String> expand) {
      paths.clear();
      paths.addAll(expand);
      counter.getAndIncrement();
   }
  
}
