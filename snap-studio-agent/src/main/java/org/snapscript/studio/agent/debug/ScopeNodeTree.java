package org.snapscript.studio.agent.debug;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.snapscript.core.scope.Scope;
import org.snapscript.core.scope.ScopeState;
import org.snapscript.core.scope.index.ScopeTable;
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
         ScopeState state = scope.getState();
         ScopeTable table = scope.getTable();
         Iterator<String> names = state.iterator();
         Iterator<Value> locals = table.iterator();
         
         if(names.hasNext() || locals.hasNext()) {
            Set<String> done = new HashSet<String>();
          
            while(locals.hasNext()) {
               Value local = locals.next();
               
               if(local != null) {
                  String name = local.getName();
                  Object object = local.getValue();
                  
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
               Value value = state.getValue(name);
               
               if(value != null) { // don't override stack locals
                  int last = name.indexOf('@');                  
                  String prefix = last != -1 ? name.substring(0, last) : name;
                  
                  if(done.add(prefix)){
                     Object object = value.getValue();
                     int modifiers = value.getModifiers();
                     ScopeNode node = builder.createNode(prefix, prefix, object, modifiers, 0);
                     
                     if(node != null) {
                        nodes.add(node);
                     }
                  }
               }
            }
         }
      }
      return nodes;
   }
}