package org.snapscript.index;

import org.snapscript.core.Path;

public class IndexResult implements Index {

   private IndexType type;
   private Object operation;
   private String name;
   private Path path;
   private int line;
   
   public IndexResult(IndexType type, Object operation, String name, Path path, int line) {
      this.operation = operation;
      this.type = type;
      this.name = name;
      this.path = path;
      this.line = line;
   }
   
   @Override
   public IndexType getType() {
      return type;
   }

   @Override
   public Object getOperation() {
      return operation;
   }
   
   public void setOperation(Object operation) {
      this.operation = operation;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public Path getPath() {
      return path;
   }

   @Override
   public int getLine() {
      return line;
   }
   
   @Override
   public String toString() {
      return String.format("%s -> %s", name, type);
   }
   
}
