package org.snapscript.studio.service.command;

public class RenameCommandMarshaller extends ObjectCommandMarshaller<RenameCommand>{
   
   public RenameCommandMarshaller() {
      super(CommandType.RENAME);
   }
}