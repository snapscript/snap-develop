package org.snapscript.studio.index.counter;

import org.snapscript.common.ArrayStack;
import org.snapscript.common.Stack;
import org.snapscript.parse.Line;
import org.snapscript.parse.Token;
import org.snapscript.parse.TokenType;

public class BraceStack {
   
   private final Stack<BraceNode> stack;
   private final BraceNode root;
   
   public BraceStack() {
      this.stack = new ArrayStack<BraceNode>();
      this.root = new BraceNode(null, null);
      this.stack.push(root);
   }
   
   public int depth(int line) {
      return root.depth(line);
   }
   
   public void update(Token token) {
      Object value = token.getValue();
      Line line = token.getLine();
      int size = stack.size();
      short type = token.getType();
      
      if((type & TokenType.LITERAL.mask) == TokenType.LITERAL.mask) {
         if(value.equals("{")){
            BraceNode current = stack.peek();
            BraceNode node = current.open(BraceType.COMPOUND, line, size);

            stack.push(node);
         }else if(value.equals("}")) {
            BraceNode current = stack.pop();
            BraceNode parent = current.getParent();
            int depth = parent.close(BraceType.COMPOUND, line);
            
            if(depth != 0) {
               throw new IllegalStateException("Bracket not closed");
            }
         }else if(value.equals("(")) {
            open(BraceType.NORMAL, line);
         }else if(value.equals(")")) {
            close(BraceType.NORMAL, line);
         }else if(value.equals("[")) {
            open(BraceType.ARRAY, line);
         }else if(value.equals("]")) {
            close(BraceType.ARRAY, line);
         }
      }
   }
   
   private void open(BraceType type, Line line) {
      BraceNode node = stack.peek();
      int depth = stack.size();
      
      if(node == null) {
         throw new IllegalStateException("No node");
      }
      node.open(type, line, depth);
   }
   
   private void close(BraceType type, Line line) {
      BraceNode node = stack.peek();
      int depth = stack.size();
      
      if(depth == 0) {
         throw new IllegalStateException("Stack is empty");
      }
      node.close(type, line);
   }
}