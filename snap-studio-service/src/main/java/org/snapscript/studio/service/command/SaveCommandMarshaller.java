package org.snapscript.studio.service.command;

public class SaveCommandMarshaller extends ObjectCommandMarshaller<SaveCommand>{
   
   public SaveCommandMarshaller() {
      super(CommandType.SAVE);
   }
}