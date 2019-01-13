package org.snapscript.studio.agent.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snapscript.core.property.Property;
import org.snapscript.core.scope.Scope;
import org.snapscript.core.scope.ScopeState;
import org.snapscript.core.scope.instance.Instance;
import org.snapscript.core.type.Type;
import org.snapscript.core.type.TypeTraverser;
import org.snapscript.core.variable.Value;

public class InstanceScopeNode implements ScopeNode {
   
   private final TypeTraverser extractor;
   private final ScopeNodeBuilder builder;
   private final List<ScopeNode> nodes;
   private final Scope scope;
   private final String path;
   private final String name;
   private final int depth;
   
   public InstanceScopeNode(ScopeNodeBuilder builder, Instance scope, String path, String name, int depth) {
      this.extractor = new TypeTraverser();
      this.nodes = new ArrayList<ScopeNode>();
      this.builder = builder;
      this.scope = scope;
      this.depth = depth;
      this.name = name;
      this.path = path;
   }
   
   @Override
   public int getDepth() {
      return depth;
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public String getPath() {
      return path;
   }

   @Override
   public List<ScopeNode> getNodes() {
      if(nodes.isEmpty()) {
         ScopeState state = scope.getState();
         Iterator<String> names = state.iterator();
         Type type = scope.getType();
         Set<Type> types = extractor.findHierarchy(type);
         
         if(names.hasNext() && !types.isEmpty()) {
            Map<String, String> include = new HashMap<String, String>();
            
            for(Type base : types) {
               List<Property> fields = base.getProperties();
               
               for(Property property : fields) {
                  String alias = property.getAlias();
                  String name = property.getName();
                  
                  include.put(alias, name);
               }
            }
            while(names.hasNext()) {
               String alias = names.next();
               
               if(include.containsKey(alias)) {
                  String name = include.get(alias);
                  Value value = state.getValue(alias);                  
                  Object object = value.getValue();
                  int modifiers = value.getModifiers();
                  ScopeNode node = builder.createNode(path + "." + alias, name, object, modifiers, depth);
                  
                  if(node != null) {
                     nodes.add(node);
                  }
               }
            }
         }
      }
      return nodes;
   }
}