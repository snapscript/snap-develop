package org.snapscript.studio.agent.debug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.snapscript.core.scope.Scope;
import org.snapscript.core.scope.State;
import org.snapscript.core.scope.Value;
import org.snapscript.core.scope.instance.Instance;
import org.snapscript.core.type.Type;
import org.snapscript.core.property.Property;
import org.snapscript.core.type.TypeTraverser;
import org.snapscript.studio.agent.debug.ScopeNode;
import org.snapscript.studio.agent.debug.ScopeNodeBuilder;

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
         Iterator<String> names = state.iterator();
         Type type = scope.getType();
         Set<Type> types = extractor.findHierarchy(type);
         
         if(names.hasNext() && !types.isEmpty()) {
            Set<String> include = new HashSet<String>();
            
            for(Type base : types) {
               List<Property> fields = base.getProperties();
               
               for(Property property : fields) {
                  String name = property.getName();
                  include.add(name);
               }
            }
            while(names.hasNext()) {
               String name = names.next();
               
               if(include.contains(name)) {
                  Value value = state.get(name); 
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