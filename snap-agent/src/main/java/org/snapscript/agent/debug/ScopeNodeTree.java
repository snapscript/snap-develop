package org.snapscript.agent.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.snapscript.core.Scope;
import org.snapscript.core.Value;
import org.snapscript.core.State;

public class ScopeNodeTree implements ScopeNode {
   
   private final ScopeNodeBuilder builder;
   private final List<ScopeNode> nodes;
   private final Scope scope;
   
   public ScopeNodeTree(ScopeNodeBuilder builder, Scope scope) {
      this.nodes = new ArrayList<ScopeNode>();
      this.builder = builder;
      this.scope = scope;
   }
   
   @Override
   public int getDepth() {
      return 0;
   }
   
   @Override
   public String getName() {
      return "";
   }
   
   @Override
   public String getPath() {
      return "";
   }
   
   @Override
   public List<ScopeNode> getNodes() {
      if(nodes.isEmpty()) {
         State state = scope.getState();

         for(String name : state) {
            Value value = state.get(name);
            
            if(value != null) {
               Object object = value.getValue();
               int modifiers = value.getModifiers();
               ScopeNode node = builder.createNode(name, name, object, modifiers, 0);
               
               if(node != null) {
                  nodes.add(node);
               }
            }
         }
      }
      return nodes;
   }
}