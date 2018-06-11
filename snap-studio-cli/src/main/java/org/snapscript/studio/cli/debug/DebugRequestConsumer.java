package org.snapscript.studio.cli.debug;

import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;

public class DebugRequestConsumer {

   private final DebugRequestListener listener;
   
   public DebugRequestConsumer(DebugRequestListener listener){
      this.listener = listener;
   }
   
   public void read(Socket socket) {
      try {
         InputStream stream = socket.getInputStream();
         ObjectInput input = new ObjectInputStream(stream);
         Object value = input.readObject();
         
         if(AttachRequest.class.isInstance(value)) {
            listener.onAttachRequest((AttachRequest)value);
         } else if(DetachRequest.class.isInstance(value)) {
            listener.onDetachRequest((DetachRequest)value);
         }
      } catch(Exception e) {
         e.printStackTrace();
      }
   }
}
