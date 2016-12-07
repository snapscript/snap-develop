package org.snapscript.develop.command;

import org.simpleframework.http.socket.Frame;
import org.simpleframework.http.socket.FrameListener;
import org.simpleframework.http.socket.FrameType;
import org.simpleframework.http.socket.Reason;
import org.simpleframework.http.socket.Session;

public class CommandController implements FrameListener {

   private final CommandListener listener;
   private final CommandReader reader;
   
   public CommandController(CommandListener listener) {
      this.reader = new CommandReader();
      this.listener = listener;
   }

   @Override
   public void onFrame(Session socket, Frame frame) {
      FrameType type = frame.getType();

      try {
         if(type == FrameType.TEXT){
            String text = frame.getText();
            Command command = reader.read(text);
            
            if(command instanceof ExecuteCommand) {
               listener.onExecute((ExecuteCommand)command);
            } else if(command instanceof AttachCommand) {
               listener.onAttach((AttachCommand)command);
            } else if(command instanceof BreakpointsCommand) {
               listener.onBreakpoints((BreakpointsCommand)command);
            } else if(command instanceof DeleteCommand) {
               listener.onDelete((DeleteCommand)command);
            } else if(command instanceof SaveCommand) {
               listener.onSave((SaveCommand)command);
            } else if(command instanceof StepCommand) {
               listener.onStep((StepCommand)command);
            } else if(command instanceof StopCommand) {
               listener.onStop((StopCommand)command);
            } else if(command instanceof BrowseCommand) {
               listener.onBrowse((BrowseCommand)command);
            } else if(command instanceof BrowseCommand) {
               listener.onBrowse((BrowseCommand)command);
            } else if(command instanceof EvaluateCommand) {
               listener.onEvaluate((EvaluateCommand)command);
            } else if(command instanceof RenameCommand) {
               listener.onRename((RenameCommand)command);
            } 
         } else if(type == FrameType.PONG){
            listener.onPing();
         }
      } catch(Throwable e){
         e.printStackTrace();
      }
   }

   @Override
   public void onError(Session socket, Exception cause) {
      cause.printStackTrace();
      listener.onClose();
   }

   @Override
   public void onClose(Session session, Reason reason) {
      listener.onClose();
   }

}
