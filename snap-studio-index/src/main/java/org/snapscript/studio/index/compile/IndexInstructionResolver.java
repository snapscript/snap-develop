package org.snapscript.studio.index.compile;

import static org.snapscript.core.Reserved.INSTRUCTION_FILE;

import java.util.concurrent.atomic.AtomicBoolean;

import org.snapscript.common.Cache;
import org.snapscript.common.CopyOnWriteCache;
import org.snapscript.core.Context;
import org.snapscript.core.type.Type;
import org.snapscript.core.type.TypeLoader;
import org.snapscript.tree.Instruction;
import org.snapscript.tree.InstructionReader;
import org.snapscript.tree.Operation;
import org.snapscript.tree.OperationResolver;

public class IndexInstructionResolver implements OperationResolver {
   
   private final String INDEX_INSTRUCTIONS_FILE = "index-instruction.txt";

   private final Cache<String, Operation> registry;
   private final InstructionReader overrides;
   private final InstructionReader reader;
   private final AtomicBoolean done;
   private final Context context;

   public IndexInstructionResolver(Context context) {
      this.registry = new CopyOnWriteCache<String, Operation>();
      this.overrides = new InstructionReader(INDEX_INSTRUCTIONS_FILE);
      this.reader = new InstructionReader(INSTRUCTION_FILE);
      this.done = new AtomicBoolean();
      this.context = context;
   }

   public Operation resolve(String name) throws Exception {
      Operation current = registry.fetch(name);
      
      if(current == null) {    
         if(!done.get()) {
            for(Instruction instruction : reader){
               Operation operation = create(instruction);
               String grammar = instruction.getName();
               
               registry.cache(grammar, operation);
            }  
            for(Instruction instruction : overrides){
               Operation operation = create(instruction);
               String grammar = instruction.getName();
               
               registry.cache(grammar, operation);
            } 
            done.set(true);
         }
         return registry.fetch(name);
      }
      return current;
   }
   
   private Operation create(Instruction instruction) throws Exception{
      TypeLoader loader = context.getLoader();
      String value = instruction.getType();
      Type type = loader.loadType(value);
      String name = instruction.getName();
      
      return new Operation(type, name);
   }
}
