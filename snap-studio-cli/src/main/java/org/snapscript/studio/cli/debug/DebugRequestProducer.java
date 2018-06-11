package org.snapscript.studio.cli.debug;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DebugRequestProducer {
   
   public DebugRequestProducer(){
      super();
   }
   
   public void write(Socket socket, AttachRequest request) {
      try {
         OutputStream stream = socket.getOutputStream();
         ObjectOutputStream output = new ObjectOutputStream(stream);   
         
         output.writeObject(request);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
   
   public void write(Socket socket, DetachRequest request) {
      try {
         OutputStream stream = socket.getOutputStream();
         ObjectOutputStream output = new ObjectOutputStream(stream);   
         
         output.writeObject(request);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}
