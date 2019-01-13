package org.snapscript.studio.agent.debug;

import java.lang.reflect.Proxy;
import java.util.Map;

import org.snapscript.core.Context;
import org.snapscript.core.convert.PrimitivePromoter;
import org.snapscript.core.convert.proxy.ProxyWrapper;
import org.snapscript.core.scope.instance.Instance;
import org.snapscript.studio.agent.debug.ArrayScopeNode;
import org.snapscript.studio.agent.debug.InstanceScopeNode;
import org.snapscript.studio.agent.debug.ObjectScopeNode;
import org.snapscript.studio.agent.debug.ScopeNode;
import org.snapscript.studio.agent.debug.ScopeNodeChecker;
import org.snapscript.studio.agent.debug.ValueData;
import org.snapscript.studio.agent.debug.ValueDataBuilder;

public class ScopeNodeBuilder {
   
   private final Map<String, Map<String, String>> variables;
   private final PrimitivePromoter promoter;
   private final ScopeNodeChecker checker;
   private final ValueDataBuilder builder;
   private final Context context;
   
   public ScopeNodeBuilder(Map<String, Map<String, String>> variables, Context context) {
      this.builder = new ValueDataBuilder(context);
      this.promoter = new PrimitivePromoter();
      this.checker = new ScopeNodeChecker();
      this.variables = variables;
      this.context = context;
   }

   public ScopeNode createNode(String path, String name, String alias, Object object, int modifiers, int depth) {
      if(object != null) {
         ProxyWrapper wrapper = context.getWrapper();
         
         if(object instanceof Proxy) {
            object = wrapper.fromProxy(object);
         }
         if(object instanceof Instance) {
            Instance instance = (Instance)object;
            ValueData data = builder.createScope(name, instance, modifiers, depth);
            Map<String, String> map = data.getData();
            
            variables.put(path, map); // put the type rather than value
            return new InstanceScopeNode(this, instance, path, name, alias, depth + 1);
         }
         Class actual = object.getClass();
         Class type = promoter.promote(actual);
         
         if(!checker.isPrimitive(type)) { 
            if(type.isArray()) {
               ValueData data = builder.createArray(name, object, modifiers, depth);
               Map<String, String> map = data.getData();
               
               variables.put(path, map); // type rather than value
               return new ArrayScopeNode(this, object, path, name, alias, depth + 1);
            } else {
               ValueData data = builder.createObject(name, object, modifiers, depth);
               Map<String, String> map = data.getData();
               
               variables.put(path, map); // type rather than value
               return new ObjectScopeNode(this, object, path, name, alias, depth + 1);
            }
         } else {
            ValueData data = builder.createPrimitive(name, object, modifiers, depth);
            Map<String, String> map = data.getData();
            
            variables.put(path, map);
         }
      } else {
         ValueData data = builder.createNull(name, object, modifiers, depth);
         Map<String, String> map = data.getData();
         
         variables.put(path, map);
      }
      return null;
   }

}