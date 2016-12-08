
package org.snapscript.agent.debug;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.snapscript.core.Context;
import org.snapscript.core.ExpressionEvaluator;
import org.snapscript.core.Scope;

public class ExpressionProcessor {

   private final Map<String, Object> results; // holds only one expression
   private final Context context;
   private final Scope scope;
   
   public ExpressionProcessor(Context context, Scope scope) {
      this.results = new ConcurrentHashMap<String, Object>();
      this.context = context;
      this.scope = scope;
   }
   
   public Object evaluate(String expression) {
      if(expression == null) {
         results.clear();
         return null;
      }
      int length = expression.length();
      
      if(length == 0) {
         results.clear();
         return null;
      }
      if(!results.containsKey(expression)) { // only evaluate once
         results.clear(); // clear all expression when changed
         
         try {
            ExpressionEvaluator evaluator = context.getEvaluator();
            Object result =  evaluator.evaluate(scope, expression);

            results.put(expression, result);
         } catch(Exception e) {
            results.put(expression, e);
            e.printStackTrace();
         }
      }
      return results.get(expression);
   }
}
