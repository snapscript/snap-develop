package org.snapscript.index;

import java.util.concurrent.Executor;

import org.snapscript.compile.assemble.OperationBuilder;
import org.snapscript.core.Context;
import org.snapscript.core.Type;
import org.snapscript.parse.Line;

public class IndexInstructionBuilder extends OperationBuilder {

   private final IndexListener listener;
   
   public IndexInstructionBuilder(IndexListener listener, Context context, Executor executor) {
      super(context, executor);
      this.listener = listener;
   }

   @Override
   public Object create(Type type, Object[] arguments, Line line) throws Exception {
      Object result = super.create(type, arguments, line);
      
      if(Index.class.isInstance(result)) {
         Index index = (Index)result;
         Object operation = index.getOperation();
         
         listener.update(index);
         return operation;
      }
      return result;
   }
}
