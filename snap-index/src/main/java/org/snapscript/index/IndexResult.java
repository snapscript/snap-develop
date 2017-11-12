package org.snapscript.index;

import org.snapscript.core.Path;
import org.snapscript.tree.constraint.Constraint;

public class IndexResult implements Index {

   private Constraint constraint;
   private IndexType type;
   private Object operation;
   private String name;
   private Path path;
   private int line;
   
   public IndexResult(IndexType type, Object operation, Constraint constraint, String name, Path path, int line) {
      this.constraint = constraint;
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
   public Constraint getConstraint() {
      return constraint;
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
