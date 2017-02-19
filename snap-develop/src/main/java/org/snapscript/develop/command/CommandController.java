/*
 * CommandController.java December 2016
 *
 * Copyright (C) 2016, Niall Gallagher <niallg@users.sf.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

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
            } else if(command instanceof PingCommand) {
               listener.onPing((PingCommand)command);
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
