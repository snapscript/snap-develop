package org.snapscript.studio.service.command;

public class TerminateCommandMarshaller extends ObjectCommandMarshaller<TerminateCommand>{

   public TerminateCommandMarshaller() {
      super(CommandType.TERMINATE);
   }
}