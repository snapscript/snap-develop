
package org.snapscript.develop.command;

public class TerminateCommandMarshaller implements CommandMarshaller<TerminateCommand>{

   @Override
   public TerminateCommand toCommand(String value) {
      int offset = value.indexOf(':');
      String process = value.substring(offset + 1);
      
      return new TerminateCommand(process);
   }

   @Override
   public String fromCommand(TerminateCommand command) {
      String process = command.getProcess();
      return CommandType.TERMINATE + ":" + process;
   }
}
