
package org.snapscript.develop.command;

public class ExitCommandMarshaller implements CommandMarshaller<ExitCommand>{

   @Override
   public ExitCommand toCommand(String value) {
      int offset = value.indexOf(':');
      String process = value.substring(offset + 1);
      
      return new ExitCommand(process);
   }

   @Override
   public String fromCommand(ExitCommand command) {
      String process = command.getProcess();
      return CommandType.EXIT + ":" + process;
   }
}
