package org.snapscript.agent.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpressionScopeNode implements ScopeNode {
   
   private final ExpressionProcessor processor;
   private final VariableNameEncoder encoder;
   private final ScopeNodeBuilder builder;
   private final List<ScopeNode> nodes;
   private final String expression;
   private final boolean refresh;
   
   public ExpressionScopeNode(ScopeNodeBuilder builder, ExpressionProcessor processor, VariableNameEncoder encoder, String expression, boolean refresh) {
      this.nodes = new ArrayList<ScopeNode>();
      this.expression = expression;
      this.processor = processor;
      this.refresh = refresh;
      this.encoder = encoder;
      this.builder = builder;
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
         Object object = processor.evaluate(expression, refresh);
         
         if(object != null) {
            String path = encoder.encode(expression);
            ScopeNode node = builder.createNode(path, expression, object, 1);
            
            return Collections.singletonList(node);
         }
      }
      return nodes;
   }
}