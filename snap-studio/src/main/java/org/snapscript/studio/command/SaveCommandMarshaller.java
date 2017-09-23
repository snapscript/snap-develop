package org.snapscript.studio.command;

public class SaveCommandMarshaller extends ObjectCommandMarshaller<SaveCommand>{
   
   public SaveCommandMarshaller() {
      super(CommandType.SAVE);
   }
}