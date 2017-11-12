package org.snapscript.studio.index;

import java.util.ArrayList;
import java.util.List;

import org.snapscript.common.ArrayStack;
import org.snapscript.common.Stack;
import org.snapscript.parse.Line;
import org.snapscript.parse.Token;

public class IndexBraceCounter {

   private final List<Token> tokens;
   
   public IndexBraceCounter(List<Token> tokens) {
      this.tokens = tokens;
   }
   
   public int getDepth(int line) {
      BraceStack stack = new BraceStack();
      
      for(Token token : tokens) {
         Line position = token.getLine();
         String path = position.getResource();
         
         try{
            stack.update(token);
         }catch(Exception e){
            throw new IllegalStateException("Unbalanced braces in " + path + " at line "+ line, e);
         }
      }
      return stack.depth(line);
   }
   
   private static class BraceStack {
   
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

      private static class BraceNode {
         
         private List<BraceNode> children;
         private Stack<BraceNode> stack;
         private BraceNode parent;
         private BraceType type;
         private Line start;
         private Line finish;
         private int depth;
         
         public BraceNode(BraceNode parent, BraceType type) {
            this.children = new ArrayList<BraceNode>();
            this.stack = new ArrayStack<BraceNode>();
            this.parent = parent;
            this.type = type;
         }
         
         public BraceNode getParent(){
            return parent;
         }
         
         public BraceNode open(BraceType type, Line line, int depth) {
            BraceNode node = new BraceNode(this, type);
            
            stack.push(node);
            node.start = line;
            node.depth = depth;
            return node;
         }
         
         public int close(BraceType type, Line line) {
            BraceNode node = stack.pop();
 
            if(node == null) {
               throw new IllegalStateException("Unbalanced braces");
            }
            if(node.type != type) {
               throw new IllegalStateException("Unbalanced braces");
            }
            if(node.type == BraceType.COMPOUND) {
               children.add(node);
            }
            node.finish = line;
            return stack.size();
         }
         
         public int depth(int line) {
            if(enclose(line)) {
               for(BraceNode node : children) {
                  int result = node.depth(line);
                  
                  if(result != -1) {
                     return result;
                  }
               }
               return depth;
            }
            return -1;
         }
         
         private boolean enclose(int line) {
            if(start == null && finish == null) {
               return true;
            }
            int begin = start.getNumber();
            int end = finish.getNumber();
            
            return line >= begin && line <= end; 
         }
         
         public int size() {
            return stack.size();
         }
         
         @Override
         public String toString() {
            return String.valueOf(type);
         }
      }
      
      private static enum BraceType {
         ARRAY("[", "]"),
         COMPOUND("{", "}"),
         NORMAL("(", ")");
         
         private final String open;
         private final String close;
         
         private BraceType(String open, String close) {
            this.open = open;
            this.close = close;
         }
      }
   }
}
