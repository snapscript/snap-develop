package org.snapscript.studio.agent.debug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.snapscript.core.scope.Scope;
import org.snapscript.core.scope.State;
import org.snapscript.core.scope.index.Local;
import org.snapscript.core.scope.index.Table;
import org.snapscript.core.variable.Value;

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
         Table table = scope.getTable();
         Iterator<String> names = state.iterator();
         Iterator<Local> locals = table.iterator();
         
         if(names.hasNext() || locals.hasNext()) {
            Set<String> done = new HashSet<String>();
          
            while(locals.hasNext()) {
               Local local = locals.next();
               
               if(local != null) {
                  Object object = local.getValue();
                  String name = local.getName();
                  
                  if(done.add(name)) { 
                     int modifiers = local.getModifiers();
                     ScopeNode node = builder.createNode(name, name, object, modifiers, 0);
                     
                     if(node != null) {
                        nodes.add(node);
                     }
                  }
               }
            }
            while(names.hasNext()) {
               String name = names.next();
               Value value = state.get(name);
               
               if(value != null && done.add(name)) { // don't override stack locals
                  Object object = value.getValue();
                  int modifiers = value.getModifiers();
                  ScopeNode node = builder.createNode(name, name, object, modifiers, 0);
                  
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