package org.snapscript.develop.command;

public class DeleteCommandMarshaller extends ObjectCommandMarshaller<DeleteCommand>{
   
   public DeleteCommandMarshaller() {
      super(CommandType.DELETE);
   }
}