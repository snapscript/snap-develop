package org.snapscript.agent.debug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.snapscript.core.TypeTraverser;
import org.snapscript.core.Scope;
import org.snapscript.core.State;
import org.snapscript.core.Type;
import org.snapscript.core.Value;
import org.snapscript.core.define.Instance;
import org.snapscript.core.property.Property;

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
         State state = scope.getState();
         Set<String> names = state.getNames();
         Type type = scope.getType();
         Set<Type> types = extractor.findHierarchy(type);
         
         if(!names.isEmpty() && !types.isEmpty()) {
            Set<String> include = new HashSet<String>();
            
            for(Type base : types) {
               List<Property> fields = base.getProperties();
               
               for(Property property : fields) {
                  String name = property.getName();
                  include.add(name);
               }
            }
            for(String name : names) {
               if(include.contains(name)) {
                  Value value = state.getValue(name); 
                  Object object = value.getValue();
                  int modifiers = value.getModifiers();
                  ScopeNode node = builder.createNode(path + "." + name, name, object, modifiers, depth);
                  
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