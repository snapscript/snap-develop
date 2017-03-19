
package org.snapscript.develop.command;

import org.simpleframework.http.socket.FrameChannel;

public class CommandClient {
   
   private final CommandWriter writer;
   private final FrameChannel channel;
   private final String project;
   
   public CommandClient(FrameChannel channel, String project) {
      this.writer = new CommandWriter();
      this.channel = channel;
      this.project = project;
   } 
   
   public void sendCommand(Command command) throws Exception {
      String message = writer.write(command);
      channel.send(message);
   }
   
   public void sendSyntaxError(String resource, String description, long time, int line) throws Exception {
      ProblemCommand command = ProblemCommand.builder()
            .project(project)
            .description(description)
            .resource(resource)
            .time(time)
            .line(line)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendProcessTerminate(String process) throws Exception {
      TerminateCommand command = TerminateCommand.builder()
            .process(process)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendAlert(String resource, String text) throws Exception {
      AlertCommand command = AlertCommand.builder()
            .resource(resource)
            .message(text)
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
   
   public void sendReloadTree() throws Exception {
      ReloadTreeCommand command = ReloadTreeCommand.builder()
            .build();
      String message = writer.write(command);
      
      channel.send(message);
   }
}
